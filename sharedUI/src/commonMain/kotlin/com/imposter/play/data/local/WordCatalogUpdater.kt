package com.imposter.play.data.local

import com.imposter.play.data.entities.CategoryEntity
import com.imposter.play.data.entities.WordEntity
import com.imposter.play.data.local.dao.CategoryDao
import com.imposter.play.data.local.dao.WordDao
import imposter.sharedui.generated.resources.Res
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class WordCatalogUpdater(
    private val appPreferences: AppPreferences,
    private val categoryDao: CategoryDao,
    private val wordDao: WordDao,
    private val ioDispatcher: CoroutineDispatcher,
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun applyPendingUpdates() = withContext(ioDispatcher) {
        val manifest = readManifest()
        require(manifest.baseVersion >= 1) { "Catalog baseVersion must be >= 1" }

        val savedVersion = appPreferences.getWordCatalogVersion()
        val appliedVersion = when {
            savedVersion == null -> {
                appPreferences.setWordCatalogVersion(manifest.baseVersion)
                manifest.baseVersion
            }

            savedVersion < manifest.baseVersion -> {
                appPreferences.setWordCatalogVersion(manifest.baseVersion)
                manifest.baseVersion
            }

            else -> savedVersion
        }

        val pendingUpdates = manifest.updates
            .sortedBy { it.toVersion }
            .filter { it.toVersion > appliedVersion }

        for (updateRef in pendingUpdates) {
            val update = readUpdate(updateRef.file)
            require(update.toVersion == updateRef.toVersion) {
                "Catalog update version mismatch: manifest=${updateRef.toVersion}, file=${update.toVersion}"
            }
            applyUpdate(update)
            appPreferences.setWordCatalogVersion(update.toVersion)
        }
    }

    private suspend fun readManifest(): WordCatalogManifest {
        val raw = readResourceText(WORD_UPDATES_MANIFEST_PATH)
        return json.decodeFromString<WordCatalogManifest>(raw)
    }

    private suspend fun readUpdate(path: String): WordCatalogUpdate {
        val raw = readResourceText(path)
        return json.decodeFromString<WordCatalogUpdate>(raw)
    }

    private suspend fun readResourceText(path: String): String = Res.readBytes(path).decodeToString()

    private suspend fun applyUpdate(update: WordCatalogUpdate) {
        val categoriesById = categoryDao.getAll().associateBy { it.id }.toMutableMap()
        val touchedCategoryIds = mutableSetOf<String>()

        for (seed in update.categories) {
            val normalizedName = seed.name.trim()
            val normalizedIcon = seed.iconRes.trim()
            require(seed.id.isNotBlank()) { "Category id cannot be blank" }
            require(normalizedName.isNotBlank()) { "Category name cannot be blank for id=${seed.id}" }
            require(normalizedIcon.isNotBlank()) { "Category icon cannot be blank for id=${seed.id}" }

            val existing = categoriesById[seed.id]
            if (existing == null) {
                val inserted = CategoryEntity(
                    id = seed.id,
                    name = normalizedName,
                    iconRes = normalizedIcon,
                    isEnabled = true,
                    displayOrder = seed.displayOrder,
                    isCustom = false,
                    wordCount = 0,
                )
                categoryDao.insert(inserted)
                categoriesById[seed.id] = inserted
            } else if (!existing.isCustom) {
                val updated = existing.copy(
                    name = normalizedName,
                    iconRes = normalizedIcon,
                    displayOrder = seed.displayOrder,
                    isCustom = false,
                )
                if (updated != existing) {
                    categoryDao.update(updated)
                    categoriesById[seed.id] = updated
                }
            }
            touchedCategoryIds += seed.id
        }

        for (seed in update.words) {
            val normalizedText = seed.text.trim()
            val normalizedHint = seed.hint?.trim()?.takeIf { it.isNotEmpty() }

            require(normalizedText.isNotEmpty()) { "Word text cannot be blank (category=${seed.categoryId})" }
            require(seed.difficultyLevel in 0..2) {
                "Word difficulty must be 0..2 (word=$normalizedText, value=${seed.difficultyLevel})"
            }
            require(categoriesById.containsKey(seed.categoryId)) {
                "Word references unknown category '${seed.categoryId}' (word=$normalizedText)"
            }

            val existing = wordDao.findByNaturalKey(
                categoryId = seed.categoryId,
                text = normalizedText,
                difficultyLevel = seed.difficultyLevel,
            )

            if (existing == null) {
                wordDao.insert(
                    WordEntity(
                        text = normalizedText,
                        hint = normalizedHint,
                        categoryId = seed.categoryId,
                        difficultyLevel = seed.difficultyLevel,
                    )
                )
            } else if (existing.hint.isNullOrBlank() && !normalizedHint.isNullOrBlank()) {
                wordDao.updateHintById(existing.id, normalizedHint)
            }

            touchedCategoryIds += seed.categoryId
        }

        for (categoryId in touchedCategoryIds) {
            val count = wordDao.getCountByCategory(categoryId)
            categoryDao.updateWordCount(categoryId, count)
        }
    }

    @Serializable
    private data class WordCatalogManifest(
        val baseVersion: Int = 1,
        val updates: List<WordCatalogUpdateRef> = emptyList(),
    )

    @Serializable
    private data class WordCatalogUpdateRef(
        val toVersion: Int,
        val file: String,
    )

    @Serializable
    private data class WordCatalogUpdate(
        val toVersion: Int,
        val categories: List<CategorySeed> = emptyList(),
        val words: List<WordSeed> = emptyList(),
    )

    @Serializable
    private data class CategorySeed(
        val id: String,
        val name: String,
        val iconRes: String,
        val displayOrder: Int = 0,
    )

    @Serializable
    private data class WordSeed(
        val text: String,
        val hint: String? = null,
        val categoryId: String,
        val difficultyLevel: Int,
    )

    private companion object {
        const val WORD_UPDATES_MANIFEST_PATH = "files/word_updates/manifest.json"
    }
}