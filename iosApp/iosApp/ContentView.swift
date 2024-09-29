import UIKit
import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {
    let finishSplash: () -> Void

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController(finishSplash: finishSplash)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    @State private var keepSplashScreen = true

    var body: some View {
        let screenWidth = UIScreen.main.bounds.width
        let imageSize = screenWidth / 2

        ZStack {
            ComposeView(finishSplash: {
                withAnimation {
                    keepSplashScreen = false
                }

            })
            .opacity(keepSplashScreen ? 0.0 : 1.0)
            .ignoresSafeArea(.all)
            .animation(.easeInOut, value: keepSplashScreen)

            Image("SplashIcon")
                .resizable()
                .scaledToFit()
                .frame(width: imageSize, height: imageSize)
                .opacity(keepSplashScreen ? 1.0 : 0.0)
                .animation(.easeInOut, value: keepSplashScreen)

        }.ignoresSafeArea(.all)
    }
}
