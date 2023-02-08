package gf.game.view

import gf.game.{Display, InputHandler}
import gf.game.controller.GardenSceneController
import gf.game.model.SharedData
import scalafx.Includes._
import scalafx.animation.AnimationTimer
import scalafx.application.{JFXApp, Platform}
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.{Cursor, Scene}
import scalafx.scene.input.KeyEvent
import scalafx.scene.layout.AnchorPane
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class GardenScene {
  def gardenScene(stage: PrimaryStage, inputHandler: InputHandler, sharedData: SharedData, display: Display, name: String): Unit ={
    val controller = new GardenSceneController(inputHandler, sharedData)
    val scene: Scene = new Scene(1344, 960) {
      content = new ListBuffer()
      content ++= Seq(controller.model.sceneBackgroundView, controller.model.dayTextView, controller.model.dayCount, controller.model.wateringCanShadowView, controller.model.wateringCanEmptyView, controller.model.spoutShadowView)
      for (i <- controller.model.clickableViews) {
        content += i
      }
      for (i <- controller.model.plantSpots.values) {
        if (i != controller.model.sp3) content += i //layer on top of sunray later
      }

      content += display.character1

      controller.setPlayerName(name)

      var character: AnchorPane = display.character1

      content ++= Seq(controller.model.sunrayView, controller.model.sp3, controller.model.houseView)
      controller.model.clickableViews ++= Seq(controller.model.graveView, controller.model.wateringCanEmptyView)

      controller.initializeItems(content, display.playerStates("inventory").asInstanceOf[List[String]],
        display.playerStates("states").asInstanceOf[Map[String, Boolean]]("isFull"))

      onKeyPressed = (ev: KeyEvent) => inputHandler.keyPress(ev)
      onKeyReleased = (ev: KeyEvent) => inputHandler.keyRelease(ev)

      val animationTimer: AnimationTimer = AnimationTimer(t => {
        if (controller.model.gameTimer > 0) {
          controller.model.second = (t - controller.model.gameTimer) / 1e9
          controller.model.Timer += controller.model.second
          inputHandler.keyInput()
          controller.checkItems(content, display.playerStates("inventory").asInstanceOf[List[String]],
            display.playerStates("states").asInstanceOf[Map[String, Boolean]]("isFull"))
          controller.checkStates(display.playerStates("states").asInstanceOf[Map[String, Boolean]],
            display.playerStates("dayCounter").asInstanceOf[Int])
          controller.checkPlants(content, character,
            display.playerStates("plantedSpots").asInstanceOf[Map[String, String]],
            display.playerStates("waterState").asInstanceOf[Map[String, Boolean]],
            display.playerStates("fertilizerSpot").asInstanceOf[String],
            display.playerStates("plantGrowth").asInstanceOf[Map[String, Map[String, Int]]],
            display.playerStates("dayCounter").asInstanceOf[Int])
          controller.checkGameComplete(stage, content, display.playerStates("plantGrowth").asInstanceOf[Map[String, Map[String, Int]]],
            display.playerStates("states").asInstanceOf[Map[String, Boolean]]("nextDay"))

          if (display.playerStates("playerList").asInstanceOf[ListBuffer[String]].size <=2 && !controller.model.bgMusic.isPlaying) {
            controller.model.bgMusic.play() //loop music
          }

          //declared inside animation timer to get updated coordinates
          val characterVal = character.localToParent(118, 200)

          //removing and adding player view logic
          if (display.playerStates("playerList").asInstanceOf[ListBuffer[String]].length > 1) {
            val playerId = display.playerStates("playerList").asInstanceOf[ListBuffer[String]].last.split("_")
            val pid = playerId(playerId.size - 2)
            if (!content.contains(display.character2)) content += display.character2
            if (name.split("_").last == pid && character != display.character2) character = display.character2
          }
          else { //do not infinitely reassign
            if (character != display.character1) character = display.character1
            if (content.contains(display.character2)) content -= display.character2
          }
          controller.setPlayerNameLayout(content, character)

          //Intro
          if (controller.model.intro && !content.contains(controller.pModel.grandpaNotePromptView)) {
            content += controller.pModel.grandpaNotePromptView
            controller.model.grandpaAudio.play()
            controller.model.intro = false
          }
          if (content.contains(controller.pModel.grandpaNotePromptView) && sharedData.escapePressed) {
            content -= controller.pModel.grandpaNotePromptView
            controller.model.grandpaAudio.stop()
            controller.model.thud.play()
            sharedData.escapePressed = false
            content += controller.pModel.instructionsView
          }
          if (content.contains(controller.pModel.instructionsView) && sharedData.escapePressed) {
            controller.model.thud.play()
            content -= controller.pModel.instructionsView
          }

          //Inventory click
          controller.openCloseInventory(content, display.playerStates("inventory").asInstanceOf[List[String]])
          controller.model.backpackView.onMousePressed = _ => {
            controller.model.inventoryClick += 1
            controller.model.inventoryAudio.play()
          }

          //Note click
          controller.openCloseNote(content, controller.model.noteView, controller.model.noteButtonView, controller.model.noteClick)
          controller.model.noteButtonView.onMousePressed = _ => {
            controller.model.noteClick += 1
            controller.model.pageFlip.play()
          }

          //Guide click
          controller.openCloseNote(content, controller.model.guideView, controller.model.guideButtonView, controller.model.guideClick)
          controller.model.guideButtonView.onMousePressed = _ => {
            controller.model.guideClick += 1
            controller.model.pageFlip.play()
          }

          //Pickup watering can
          controller.model.wateringCanEmptyView.onMousePressed = _ => {
            if (controller.checkIntersection(characterVal, 613, 804, 160, 160)) {
              controller.displayPrompt(controller.pModel.wcPromptView, controller.pickupWateringCan, content)
            }
          }

          //Grave - fertilizer click
          controller.model.graveView.onMousePressed = _ => {
            if (controller.checkIntersection(characterVal, 0, 160, 270, 340)) {
              if (!controller.model.inventoryItems.contains(controller.model.fertilizerText)) {
                controller.model.thud.play()
                controller.model.fertilizerCounter += 1
              }
            }
          }

          //House - progress to next day
          if (!controller.model.states("isPlanting") && !controller.model.states("isWatering") && !controller.model.states("isFilling") && !controller.model.states("nextDay")) {
            if (controller.checkIntersection(characterVal, 771, 560, 50, 290)) {
              controller.displayPrompt(controller.pModel.nextDayPromptView, controller.nextDay, content)
            }
          }

          if (controller.model.states("nextDay")) {
            if (!content.contains(controller.model.blackScreen)) {
              content += controller.model.blackScreen
            }
            if (content.contains(controller.pModel.nextDayPromptView) &&
              content.contains(controller.pModel.yesButtonView) && content.contains(controller.pModel.noButtonView)) {
              content --= Seq(controller.pModel.nextDayPromptView, controller.pModel.yesButtonView, controller.pModel.noButtonView)
            }
            //TODO
            if (controller.model.blackScreen.opacity.value >= 1.0 || controller.model.screenTicker) {
              controller.model.blackScreen.opacity.value -= controller.model.opacityCounter
              controller.model.screenTicker = true
            }
            else if (controller.model.blackScreen.opacity.value <= 0.0 && controller.model.screenTicker) {
              controller.model.blackScreen.opacity.value += controller.model.opacityCounter
            }
            else {
              controller.model.blackScreen.opacity.value += controller.model.opacityCounter
            }
            controller.model.alertTimer += controller.model.second
            //TODO: Blackout animation
            if (controller.model.alertTimer >= 3) {
              controller.model.riseAndShine.play()
              controller.model.screenTicker = false
              controller.checkDayCounter(content, display.playerStates("dayCounter").asInstanceOf[Int])
              inputHandler.setState("nextDay", false)
            }
          }
          else {
            controller.model.screenTicker = false
            controller.checkDayCounter(content, display.playerStates("dayCounter").asInstanceOf[Int])
          }

          //Water spout click - allow clicks on both spout handle and spout body
          controller.model.spoutView.onMousePressed = _ => controller.pressSpout(characterVal, content)
          controller.model.spoutStateView.onMousePressed = _ => controller.pressSpout(characterVal, content)

          //cant check inside condition below because the sound always stacks
          if (controller.model.states("isFilling") && !controller.model.refilling.isPlaying) controller.model.refilling.play()

          //Refilling water
          if (controller.model.states("isFilling")) {
            controller.disableSelectedInteraction(true, controller.model.backpackView)
            controller.model.alertTimer += controller.model.second
            if (content.contains(controller.model.wateringCanEmptyView)) {
              content -= controller.model.wateringCanEmptyView
            }
            controller.model.inventoryItems(controller.model.wateringCanText) = controller.model.wateringCanFullView
            controller.model.spoutStateView.setImage(controller.model.spoutOn)
            controller.model.spoutStateView.setX(442)
            if (controller.model.alertTimer >= 3) {
              controller.turnOffSpout()
              inputHandler.setState("isFilling", false)
            }
          }
          else { //ensures that animation stops when isFilling is false
            controller.turnOffSpout()
          }

          //Fertilizer discovery
          if (controller.model.fertilizerCounter >= 5 && !controller.model.fertilizerCollected) {
            controller.model.alertTimer += controller.model.second
            if (!content.contains(controller.model.fertilizerAlertView)) {
              controller.model.youFoundMe.play()
              inputHandler.addToInventory("fertilizer")
              content += controller.model.fertilizerAlertView
              controller.disableAllInteraction(true)
              controller.model.inventoryItems += (controller.model.fertilizerText -> controller.model.fertilizerView)
            }
            if (controller.model.alertTimer >= 3) {
              if (content.contains(controller.model.fertilizerAlertView)) {
                content -= controller.model.fertilizerAlertView
                controller.disableAllInteraction(false)
              }
              controller.model.alertTimer = 0.0
              controller.model.fertilizerCollected = true
            }
          }

          //Planting
          for (spotView <- controller.model.plantSpots.values) {
            if (display.playerStates("plantedSpots").asInstanceOf[Map[String, String]].size == 3){
              spotView.cursor = Cursor.Default
            }
            spotView.onMousePressed = _ => {
              if (display.playerStates("plantedSpots").asInstanceOf[Map[String, String]].size != 3) {
                controller.validatePromptViews(content, Seq(controller.pModel.plantPromptView, controller.pModel.yesButtonView, controller.pModel.noButtonView))
                if (content.contains(controller.pModel.plantPromptView)) {
                  controller.disableAllInteraction(true)
                  controller.pModel.yesButtonView.onMousePressed = _ => {
                    if (controller.model.states("isPlanting")) { //do not allow simultaneous planting
                      content --= Seq(controller.pModel.plantPromptView, controller.pModel.yesButtonView, controller.pModel.noButtonView)
                      controller.displayOkPrompt(controller.pModel.playerPlantingPromptView, content)
                    }
                    else {
                      controller.model.chosenSoil = spotView
                      controller.model.inventoryClick += 1
                      controller.model.isPlanting = true
                      inputHandler.setState("isPlanting", true)
                      content --= Seq(controller.pModel.plantPromptView, controller.pModel.yesButtonView, controller.pModel.noButtonView)
                    }
                  }
                  controller.pModel.noButtonView.onMousePressed = _ => {
                    content --= Seq(controller.pModel.plantPromptView, controller.pModel.yesButtonView, controller.pModel.noButtonView)
                    controller.disableAllInteraction(false)
                  }
                }
              }
            }
          }

          if (controller.model.isPlanting) { //only for user who is planting
            controller.model.plantOverlays.values.foreach(i => i("seedPack").cursor = Cursor.Hand)
            controller.disableAllInteraction(false)
            controller.disableSelectedInteraction(true, controller.model.backpackView)
            //allow user to close inventory and exit planting state
            controller.model.backpackView.onMousePressed = _ => {
              controller.model.inventoryClick += 1
              controller.disableSelectedInteraction(false, controller.model.backpackView)
              inputHandler.setState("isPlanting", false)
              controller.model.isPlanting = false
            }
          }
          else {
            controller.model.plantOverlays.values.foreach(i => i("seedPack").cursor = Cursor.Default)
          }

          for ((k, v) <- controller.model.plantOverlays) {
            v("seedPack").onMousePressed = _ => {
              if (controller.model.isPlanting) {
                controller.model.inventoryClick += 1
                val spot = controller.model.plantSpots.find(_._2 == controller.model.chosenSoil).map(_._1).get
                val seedPack = controller.model.inventoryItems.find(_._2 == v("seedPack")).map(_._1).get
                val seedText = controller.model.inventoryMap.find(_._2 == seedPack).map(_._1).get
                inputHandler.updatePlantedSpots(spot, k)
                inputHandler.updateWaterState(k, false)
                inputHandler.removeFromInventory(seedText)
                controller.disableSelectedInteraction(false, controller.model.backpackView)
                inputHandler.setState("isPlanting", false)
                controller.model.isPlanting = false
              }
            }
          }

          //Watering and fertilizer
          for ((k, v) <- controller.model.plantOverlays) {
            v("soilOverlay").onMousePressed = _ => {
              if (controller.model.clickableViews.contains(v("soilOverlay"))) {
                controller.model.chosenSoil = v("soilOverlay")
                //allow watering if hasn't been watered
                if (!display.playerStates("waterState").asInstanceOf[Map[String, Boolean]](k)) {
                  controller.validatePromptViews(content, Seq(controller.pModel.waterPromptView, controller.pModel.yesButtonView, controller.pModel.noButtonView))
                  if (content.contains(controller.pModel.waterPromptView)) {
                    controller.disableAllInteraction(true)
                    controller.pModel.yesButtonView.onMousePressed = _ => {
                      if (controller.model.states("isWatering")) {
                        content --= Seq(controller.pModel.waterPromptView, controller.pModel.yesButtonView, controller.pModel.noButtonView)
                        controller.displayOkPrompt(controller.pModel.playerWateringPromptView, content)
                      }
                      else if (!controller.model.inventoryItems.contains(controller.model.wateringCanText)) { //no watering can
                        content --= Seq(controller.pModel.waterPromptView, controller.pModel.yesButtonView, controller.pModel.noButtonView)
                        controller.displayOkPrompt(controller.pModel.noWCPromptView, content)
                      }
                      else if (!controller.model.states("isFull")) { //empty watering can
                        content --= Seq(controller.pModel.waterPromptView, controller.pModel.yesButtonView, controller.pModel.noButtonView)
                        controller.displayOkPrompt(controller.pModel.emptyPromptView, content)
                      }
                      else { //full watering can
                        inputHandler.setState("isWatering", true)
                        inputHandler.updateWaterState(k, true)
                        content --= Seq(controller.pModel.waterPromptView, controller.pModel.yesButtonView, controller.pModel.noButtonView)
                      }
                    }
                    controller.pModel.noButtonView.onMousePressed = _ => {
                      content --= Seq(controller.pModel.waterPromptView, controller.pModel.yesButtonView, controller.pModel.noButtonView)
                      controller.disableAllInteraction(false)
                    }
                  }
                }
                //allow fertilizer if watered
                else {
                  if (controller.model.states.contains("fertilizerUsed") && !controller.model.states("fertilizerUsed")
                    && controller.model.inventoryItems.contains(controller.model.fertilizerText)) {
                    controller.displayPrompt(controller.pModel.useFertilizerPromptView, controller.useFertilizer, content)
                  }
                }
              }
            }
          }
          if (controller.model.states("isWatering") && !controller.model.wateringAudio.isPlaying) controller.model.wateringAudio.play()
          if (controller.model.states("isWatering")) {
            controller.disableAllInteraction(true)
            controller.model.alertTimer += controller.model.second
            controller.model.inventoryItems(controller.model.wateringCanText) = controller.model.wateringCanEmptyView
            controller.model.wateringCanFullView.setX(controller.model.chosenSoil.x.value + 45)
            controller.model.wateringCanFullView.setY(controller.model.chosenSoil.y.value - 60)
            if (!content.contains(controller.model.wateringCanFullView)) {
              content += controller.model.wateringCanFullView
            }
            if (controller.model.alertTimer >= 2) {
              controller.removeWateringCan(content)
              inputHandler.setState("isWatering", false)
            }
          }
          else {
            controller.removeWateringCan(content)
          }

        }
        controller.model.gameTimer = t
      })
      animationTimer.start
    }
    stage.setScene(scene)
  }
}
