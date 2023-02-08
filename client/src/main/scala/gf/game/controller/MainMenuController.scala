package gf.game.controller
import akka.actor.ActorSystem
import akka.stream.scaladsl.Source
import akka.stream.{ActorMaterializer, OverflowStrategy}
import javafx.collections.ObservableList
import java.net.NetworkInterface
import gf.game.model.{MainMenuModel, PromptModel, SharedData}
import gf.game.view.{GardenScene, RoomFull}
import gf.game.{Client, Display, InputHandler}
import javafx.scene.Node
import scalafx.application.JFXApp.PrimaryStage
import scala.util.matching.Regex
import scalafx.Includes._
import scalafx.scene.image.ImageView
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration
import scala.concurrent.Await

class MainMenuController {
   val model = new MainMenuModel()
   val pModel = new PromptModel()

   model.nameField.textProperty().addListener(_ => {
      val pattern: Regex = "[^a-zA-Z0-9]".r
      if (model.nameField.getText.length > 10) {
         val name = model.nameField.getText.substring(0, 10)
         model.nameField.setText(name)
      }
      val textMatch = pattern.findAllIn(model.nameField.getText)
      if (textMatch.nonEmpty){
         val name = model.nameField.getText.substring(0, textMatch.start)
         model.nameField.setText(name)
      }
   })

   def disableInteraction(value: Boolean): Unit ={
      model.playButtonView.setDisable(value)
      model.nameField.setDisable(value)
   }

   def displayPrompt(content: ObservableList[Node], prompt: ImageView): Unit ={
      content ++= Seq(prompt, pModel.okButtonView)
      pModel.okButtonView.onMousePressed = _ => {
         content --= Seq(prompt, pModel.okButtonView)
         disableInteraction(false)
      }
   }

   def initializeGardenScene(stage: PrimaryStage, content: ObservableList[Node]): Unit ={
      try {
         val SharedData = new SharedData()
         implicit val system: ActorSystem = ActorSystem()
         implicit val materializer: ActorMaterializer = ActorMaterializer()
         //to uniquely identify user's character - especially for intersection
         val id = NetworkInterface.getNetworkInterfaces.toString.split("@").last
         val name = s"${model.nameField.getText}_$id"
         val client = new Client(name)
         val display = new Display(SharedData, name)
         val input = Source.actorRef[String](5, OverflowStrategy.dropNew)
         val output = display.sink
         val ((inputMat, result), _) = client.run(input, output)
         val inputHandler = new InputHandler(inputMat, SharedData)
         Await.result(result,Duration(10, TimeUnit.SECONDS))
         new GardenScene().gardenScene(stage,inputHandler,SharedData,display,name)
      }
      catch {
         case ex: NoSuchElementException => new RoomFull().roomFull(stage)
         case e: RuntimeException => displayPrompt(content,pModel.serverErrorPromptView)
      }
   }

}
