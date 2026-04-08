import androidx.compose.ui.window.ComposeUIViewController
import com.imposter.play.App
import com.imposter.play.di.initKoin
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    initKoin()
    return ComposeUIViewController {
        App()
    }
}
