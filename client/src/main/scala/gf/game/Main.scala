package gf.game
import gf.game.view.MainMenu
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.image.Image

object Main extends JFXApp{
  stage = new PrimaryStage {
    title.value = "Grandpa's Fertilizer"
    resizable = false
    scene = new Scene(1344, 960)
  }
  stage.getIcons.add(new Image(getClass.getResourceAsStream("/game-logo.png")))

  new MainMenu().mainMenu(stage)
}
