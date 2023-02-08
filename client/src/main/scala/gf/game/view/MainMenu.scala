package gf.game.view

import gf.game.controller.MainMenuController
import scalafx.Includes._
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class MainMenu {
  def mainMenu(stage: PrimaryStage): Unit ={
    val controller = new MainMenuController()
    val scene: Scene = new Scene(1344, 960) {
      content = new ListBuffer()
      content ++= Seq(controller.model.menuBackgroundView, controller.model.playButtonView, controller.model.nameField)
      controller.model.playButtonView.onMousePressed = _ => {
        controller.disableInteraction(true)
        controller.initializeGardenScene(stage, content)
      }
    }
    stage.setScene(scene)
  }
}
