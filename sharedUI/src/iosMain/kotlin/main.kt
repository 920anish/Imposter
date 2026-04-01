import androidx.compose.ui.window.ComposeUIViewController
import com.imposter.play.App
import com.imposter.play.di.initKoin
import platform.UIKit.UIViewController
import platform.Foundation.NSHomeDirectory

fun MainViewController(): UIViewController {
    initKoin(prefsPath = "${NSHomeDirectory()}/Documents/imposter_prefs.json")
    return ComposeUIViewController {
        App()
    }
}
