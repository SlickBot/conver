import SwiftUI
import ConverApp

// Hosts the shared Compose UI. `MainViewController()` is the top-level Kotlin function in
// MainViewController.kt (exposed to Swift as MainViewControllerKt.MainViewController()); it
// starts Koin once and returns a ComposeUIViewController rendering the shared App().
struct ComposeView: UIViewControllerRepresentable {
  func makeUIViewController(context: Context) -> UIViewController {
    MainViewControllerKt.MainViewController()
  }

  func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
  var body: some View {
    ComposeView()
      .ignoresSafeArea(.keyboard) // Compose has its own keyboard handler
  }
}
