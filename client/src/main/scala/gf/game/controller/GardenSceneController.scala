package gf.game.controller
import gf.game.InputHandler
import gf.game.model.{GardenSceneModel, PromptModel, SharedData}
import gf.game.view.MainMenu
import javafx.collections.ObservableList
import scalafx.geometry.Point2D
import scalafx.Includes._
import scalafx.scene.image.{Image, ImageView}
import javafx.scene.Node
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Cursor

import scala.collection.mutable.ListBuffer
import scalafx.scene.layout.AnchorPane

import scala.collection.immutable.Map

class GardenSceneController(inputHandler: InputHandler, sharedData: SharedData){
  val model = new GardenSceneModel()
  val pModel = new PromptModel()

  def setPlayerName(name: String): Unit ={
    val pName = name.split("_")
    if (pName.size >= 2) model.playerName.text = pName.head
  }

  def setPlayerNameLayout(content: ObservableList[Node], character: AnchorPane):Unit = {
    val text = model.playerName.text.value
    if (text != ""){
      model.playerName.setX(character.layoutX.value + 13 + ((10 - text.length) * 5))
      model.playerName.setY(character.layoutY.value)
      if (!content.contains(model.playerName)) content += model.playerName
    }
  }

  def checkGameComplete(stage: PrimaryStage, content: ObservableList[Node], plantGrowth: Map[String, Map[String, Int]], nextDay: Boolean): Unit ={
    if (!nextDay && plantGrowth.values.forall(v => v("daysLeft") == 1)){
      validatePromptViews(content, Seq(pModel.gameCompleteView,pModel.exitButtonView))
      if (content.contains(pModel.gameCompleteView)) {
        pModel.exitButtonView.onMousePressed = _ => {
          System.exit(0)
        }
      }
    }
  }

  def initializeItems(content: ObservableList[Node], playerInventory: List[String], isFull: Boolean): Unit = {
    for ((k, v) <- model.inventoryMap) {
      //remove from inventory map if not in player inventory at initial state
      if (!playerInventory.contains(k)) {
        model.inventoryItems -= v
      }
      else if (playerInventory.contains(k) && k == "wateringCan") {
        removeWateringCanEmpty(content)
        if (isFull){
          model.wateringCanFullView.setY(479)
          model.inventoryItems += (model.wateringCanText -> model.wateringCanFullView)
        }
      }
    }
  }

  def removeWateringCanEmpty(content: ObservableList[Node]): Unit = {
    content --= Seq(model.wateringCanShadowView, model.wateringCanEmptyView)
    model.wateringCanEmptyView.setY(486)
    model.wateringCanEmptyView.cursor = Cursor.Default
  }

  def checkStates(playerStates: Map[String, Boolean], dayCounter: Int): Unit = {
    for ((state, value) <- playerStates) {
      if (model.states(state) != value) {
        model.states(state) = value
      }
    }
  }

  def checkItems(content: ObservableList[Node], playerInventory: List[String], isFull: Boolean): Unit = {
    if (playerInventory.contains("wateringCan")) {
      //watering can not added to inventory items yet
      if (!model.inventoryItems.contains(model.wateringCanText)){
        removeWateringCanEmpty(content)
        model.inventoryItems += (model.wateringCanText -> model.wateringCanEmptyView)
      }
    }
    if (playerInventory.contains("fertilizer") && model.fertilizerCounter < 5 && !model.fertilizerCollected && !model.inventoryItems.contains(model.fertilizerText)){
      model.fertilizerCollected = true
      model.inventoryItems += (model.fertilizerText -> model.fertilizerView)
    }
  }

  def checkPlants(content: ObservableList[Node], character:AnchorPane,
                  plantedSpots: Map[String,String], waterState: Map[String, Boolean],
                  fertilizerSpot: String, plantGrowth: Map[String, Map[String, Int]], dayCounter: Int): Unit = {
    for ((spot, spotView) <- model.plantSpots){
      if (plantedSpots.contains(spot)) {
        val soilOverlay = model.plantOverlays(plantedSpots(spot))("soilOverlay")
        val waterOverlay = model.plantOverlays(plantedSpots(spot))("waterOverlay")
        val plantView = model.plantOverlays(plantedSpots(spot))("plantView") //e.g. carrotView
        setPlantState(plantView, soilOverlay, plantGrowth(plantedSpots(spot)))
        if (!content.contains(soilOverlay) && !content.contains(plantView)){ //if planted but doesnt have an overlay yet
          //view order: plant view (seed) -> soil overlay
          sharedData.mudAudio.play()
          soilOverlay.setX(spotView.x.value)
          soilOverlay.setY(spotView.y.value)
          plantView.setX(spotView.x.value + plantView.getImage.getHeight)
          plantView.setY(spotView.y.value - plantView.getImage.getHeight + 40)
          //inserts behind both players and sunray
          if (content(content.indexOf(character) - 1).typeSelector == "AnchorPane") {
            content.insert(content.indexOf(character) - 1, plantView)
          }
          else {
            content.insert(content.indexOf(character), plantView)
          }
          content += soilOverlay
          model.clickableViews += soilOverlay
        }
        if (waterState.contains(plantedSpots(spot))){
          if (waterState(plantedSpots(spot))){
            // if watered but content doesnt have a water overlay yet
            if (!model.fertilizerCollected || fertilizerSpot != "") soilOverlay.cursor = Cursor.Default else soilOverlay.cursor = Cursor.Hand
            if (!content.contains(waterOverlay)){
              model.chosenSoil = soilOverlay
              waterOverlay.setX(soilOverlay.x.value - 17)
              waterOverlay.setY(soilOverlay.y.value - 7)
              //insert behind plant view
              //view order: water overlay -> plant view -> soil overlay
              content.insert(content.indexOf(plantView), waterOverlay)
              model.wateringCanFullView.setY(479) //return to inventory y-axis display
              inputHandler.setState("isFull", false)
            }
            //if watered and next day - update plant growth
            if (model.dayCount.text.value != dayCounter.toString){
              if (plantGrowth(plantedSpots(spot))("daysLeft") == 0){  // if in seed stage
                // reduce days by 2 if sunflower is planted under direct sun during seed stage
                if (spot == "sp3" && plantedSpots(spot) == "soilOverlaySunflower") {
                  inputHandler.updatePlantGrowth(plantedSpots(spot), 5, 5)
                }
                else {
                  inputHandler.updatePlantGrowth(plantedSpots(spot), model.plantGrowthRate(plantedSpots(spot)), model.plantGrowthRate(plantedSpots(spot)))
                }
              }
              else if (plantGrowth(plantedSpots(spot))("daysLeft") > 1) {
                inputHandler.updatePlantGrowth(plantedSpots(spot), plantGrowth(plantedSpots(spot))("daysRequired"), plantGrowth(plantedSpots(spot))("daysLeft") - 1)
                if (fertilizerSpot == plantedSpots(spot) && content.contains(model.fertilizerOverlayView) && plantGrowth(plantedSpots(spot))("daysLeft") != 1) {
                  //reduce days by 1 if has fertilizer. no effect if days left = 1
                  inputHandler.updatePlantGrowth(plantedSpots(spot), plantGrowth(plantedSpots(spot))("daysRequired"), plantGrowth(plantedSpots(spot))("daysLeft") - 1)
                }
              }
              inputHandler.updateWaterState(plantedSpots(spot), false)
            }
          }
          //remove overlay for next day if exists
          else if (!waterState(plantedSpots(spot)) && content.contains(waterOverlay)){
            soilOverlay.cursor = Cursor.Hand
            content -= content(content.indexOf(plantView) - 1)  //remove water overlay
          }
        }
        // apply fertilizer
        if (fertilizerSpot != "" && model.plantOverlays(fertilizerSpot)("soilOverlay") == soilOverlay && !content.contains(model.fertilizerOverlayView)){
          model.fertilizerOverlayView.setX(soilOverlay.x.value + 10)
          model.fertilizerOverlayView.setY(soilOverlay.y.value + 2)
          sharedData.mudAudio.play()
          //insert behind water overlay
          //view order: fertilizer -> water overlay -> plant view -> soil overlay
          content.insert(content.indexOf(plantView) - 1, model.fertilizerOverlayView)
        }
      }
    }
  }

  def checkIntersection(character: Point2D, intersectX: Int, intersectY: Int, width: Int, height: Int): Boolean = {
    val x = intersectX.toDouble - character.x
    val y = intersectY.toDouble - character.y
    val state = if ((x <= 0 && x >= (width - (width * 2))) && (y <= 0 && y >= (height - (height * 2)))) true else false
    state
  }

  def openCloseInventory(content: ObservableList[Node], playerInventory: List[String]): Unit ={
    if (model.inventoryClick % 2 == 0 && content.contains(model.inventoryView)) { //Close inventory
      disableSelectedInteraction(false, model.backpackView)
      for ((k, v) <- model.inventoryItems) {
        content --= Seq(k, v)
      }
      if (content.contains(model.emptyText)) {
        content -= model.emptyText
      }
      else if (content.contains(model.fullText)) {
        content -= model.fullText
      }
      model.inventoryCounter = 0
      content -= model.inventoryView
    }
    else if (model.inventoryClick % 2 != 0 && !content.contains(model.inventoryView)) { //Open inventory
      disableSelectedInteraction(true, model.backpackView)
      content += model.inventoryView
      for ((k, text) <- model.inventoryMap){
        if (playerInventory.contains(k)){
          val itemView = model.inventoryItems(text)
          text.setX(352 + model.inventoryCounter - ((text.toString().length % 10) * 4)) //center align text
          itemView.setX(352 + model.inventoryCounter)
          content ++= Seq(text, itemView)
          if (itemView == model.wateringCanEmptyView) {
            model.emptyText.setX(itemView.x.value)
            content += model.emptyText
          }
          else if (itemView == model.wateringCanFullView) {
            model.fullText.setX(itemView.x.value)
            content += model.fullText
          }
          model.inventoryCounter += 140
        }
      }
    }
  }

  def openCloseNote(content: ObservableList[Node], item: ImageView, button: ImageView, clickVal: Int): Unit = {
    if (clickVal % 2 == 0 && content.contains(item)) { //Close inventory
      disableSelectedInteraction(false, button)
      content -= item
    }
    else if (clickVal % 2 != 0 && !content.contains(item)) { //Open inventory
      disableSelectedInteraction(true, button)
      content += item
    }
  }

  def disableAllInteraction(value: Boolean): Unit ={
    for (i <- model.clickableViews) {
      i.setDisable(value)
    }
    for (i <- model.plantSpots.values) {
      i.setDisable(value)
    }
  }

  def disableSelectedInteraction(value: Boolean, item: ImageView): Unit ={
    for (i <- model.clickableViews) {
      if (i != item) i.setDisable(value)
    }
    for (i <- model.plantSpots.values) {
      i.setDisable(value)
    }
  }

  def validatePromptViews(content: ObservableList[Node], views: Seq[ImageView]): Unit = {
    if (!views.exists(content.contains(_))) views.foreach(content += _)
  }

  def displayPrompt(prompt: ImageView, yesFunction: ObservableList[Node] => Unit, content: ObservableList[Node]): Unit ={
    validatePromptViews(content, Seq(prompt, pModel.yesButtonView, pModel.noButtonView))
    if (content.contains(prompt)) {
      disableAllInteraction(true)
      pModel.yesButtonView.onMousePressed = _ => {
        yesFunction(content)
        content --= Seq(prompt, pModel.yesButtonView,pModel.noButtonView)
      }
      pModel.noButtonView.onMousePressed = _ => {
        content --= Seq(prompt, pModel.yesButtonView,pModel.noButtonView)
        disableAllInteraction(false)
      }
    }
  }

  def displayOkPrompt(prompt: ImageView, content: ObservableList[Node]): Unit = {
    validatePromptViews(content, Seq(prompt, pModel.okButtonView))
    if (content.contains(prompt)) {
      disableAllInteraction(true)
      pModel.okButtonView.onMousePressed = _ => {
        content --= Seq(prompt, pModel.okButtonView)
        disableAllInteraction(false)
      }
    }
  }

  def growPlant(plantView: ImageView, soilOverlay: ImageView, image: Image): Unit = {
    plantView.setImage(image)
    plantView.setX(soilOverlay.x.value + ((120-plantView.getImage.getWidth)/2))
    if (plantView == model.cabbageView && plantView.getImage.getHeight >= 59){
      plantView.setY(soilOverlay.y.value - plantView.getImage.getHeight + 80)
    }
    else {
      plantView.setY(soilOverlay.y.value - plantView.getImage.getHeight + 40)
    }
  }

  def setPlantState(plantView: ImageView, soilOverlay: ImageView, plantGrowth: Map[String, Int]): Unit ={
    if (plantGrowth("daysLeft") == 0) {
      growPlant(plantView, soilOverlay, model.plantStages(plantView)("seed"))
    }
    else if (plantGrowth("daysLeft") == 1) {
      growPlant(plantView, soilOverlay, model.plantStages(plantView)("grown"))
    }
    else if (plantGrowth("daysLeft") == (plantGrowth("daysRequired").toFloat / 2).ceil) { //midpoint
      growPlant(plantView, soilOverlay, model.plantStages(plantView)("medium"))
    }
    else if (plantGrowth("daysLeft") == plantGrowth("daysRequired")){
      growPlant(plantView, soilOverlay, model.plantStages(plantView)("sprout"))
    }
  }

  def checkDayCounter(content: ObservableList[Node], dayCounter: Int): Unit = {
    if (model.dayCount.text.value != dayCounter.toString) {
      model.dayCount.text = dayCounter.toString
      disableAllInteraction(false)
      model.alertTimer = 0.0
    }
    if (content.contains(model.blackScreen)){
      content -= model.blackScreen
    }
    disableAllInteraction(false)
  }

  def nextDay(content: ObservableList[Node]): Unit ={
    model.plantOverlays.values.foreach(i => i("soilOverlay").cursor = Cursor.Hand)
    disableAllInteraction(true)
    inputHandler.setState("nextDay", true)
    inputHandler.updateDayCounter()
  }

  def useFertilizer(content: ObservableList[Node]): Unit = {
    if (model.states("fertilizerUsed")){
      displayOkPrompt(pModel.fertilizerUsedPromptView, content)
    }
    else {
      inputHandler.setState("fertilizerUsed", true)
      val soilOverlay = model.plantOverlays.find(_._2("soilOverlay") == model.chosenSoil).map(_._1).get
      inputHandler.setFertilizerSpot(soilOverlay)
      inputHandler.removeFromInventory("fertilizer")
      disableAllInteraction(false)
    }
  }

  def pickupWateringCan(content: ObservableList[Node]): Unit ={
    model.pop.play()
    inputHandler.addToInventory("wateringCan")
    disableAllInteraction(false)
  }

  def removeWateringCan(content: ObservableList[Node]): Unit = {
    if (content.contains(model.wateringCanFullView) && !content.contains(model.inventoryView)) {
      content -= model.wateringCanFullView
      model.wateringCanFullView.setY(479)
      disableAllInteraction(false)
      model.alertTimer = 0.0
    }
  }

  def refillWater(content: ObservableList[Node]): Unit = {
    if (model.states("isFull")) { //check if other player is already filling
      displayOkPrompt(pModel.fullPromptView, content)
    }
    else {
      inputHandler.setState("isFilling", true)
      inputHandler.setState("isFull", true)
    }
  }

  def turnOffSpout(): Unit = {
    if (model.spoutStateView.x.value != 530){
      model.spoutStateView.setImage(model.spoutOff)
      model.spoutStateView.setX(530)
      disableSelectedInteraction(false, model.backpackView)
      model.alertTimer = 0.0
    }
  }

  def pressSpout(character: Point2D, content: ObservableList[Node]): Unit = {
    if (checkIntersection(character, 412, 166, 360, 400)){
      if (!model.inventoryItems.contains(model.wateringCanText)) {
        displayOkPrompt(pModel.noWCPromptView, content)
      }
      else if (model.states("isFull")) {  //do not allow refill if full
        displayOkPrompt(pModel.fullPromptView, content)
      }
      else {
        displayPrompt(pModel.refillPromptView, refillWater, content)
      }
    }
  }



}
