package gf.game.model

import scalafx.scene.media.AudioClip
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.text.{Font, Text}
import javafx.scene.{text => jfxst}
import scalafx.scene.Cursor
import scalafx.scene.paint.Color
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scalafx.scene.shape.Rectangle

class GardenSceneModel {
  val opacityCounter = 0.005 // 1/200 roughly this value render around 180-200 frame in 3sec so double of that 0 - 1 and 1 - 0
  var screenTicker = false
  var inventoryClick = 0
  var inventoryCounter = 0
  var noteClick = 0
  var guideClick = 0
  var fertilizerCounter = 0
  var fertilizerCollected = false
  var second = 0.0
  var isPlanting = false //local
  var intro = true

  //timers
  var gameTimer = 0L
  var Timer = 0.0
  var alertTimer = 0.5

  val bgMusic = new AudioClip(getClass.getResource("/gf.game.model/backgroundMusic.mp3").toExternalForm){
    volume = 0.15
  }

  val grandpaAudio = new AudioClip(getClass.getResource("/gf.game.model/grandpa-audio.mp3").toExternalForm) {
    volume = 0.2
  }
  val pop = new AudioClip(getClass.getResource("/gf.game.model/pop.mp3").toExternalForm) {
    volume = 0.2
  }
  val thud = new AudioClip(getClass.getResource("/gf.game.model/thud.mp3").toExternalForm) {
    volume = 0.1
  }
  val inventoryAudio = new AudioClip(getClass.getResource("/gf.game.model/inventory.mp3").toExternalForm) {
    volume = 0.05
  }
  val pageFlip = new AudioClip(getClass.getResource("/gf.game.model/pageflip.mp3").toExternalForm) {
    volume = 0.2
  }
  val refilling = new AudioClip(getClass.getResource("/gf.game.model/refilling.mp3").toExternalForm) {
    volume = 0.1
  }
  val wateringAudio = new AudioClip(getClass.getResource("/gf.game.model/wateringAudio.mp3").toExternalForm) {
    volume = 0.3
  }
  val riseAndShine = new AudioClip(getClass.getResource("/gf.game.model/riseShine.mp3").toExternalForm) {
    volume = 0.2
  }
  val youFoundMe = new AudioClip(getClass.getResource("/gf.game.model/youFoundMe.mp3").toExternalForm) {
    volume = 0.2
  }

  val sceneBackground = new Image(getClass.getResourceAsStream("/gf.game.model/scene.png"))
  val sceneBackgroundView: ImageView = new ImageView(sceneBackground) {
    fitWidth = 1344
    fitHeight = 960
  }

  val blackScreen: Rectangle = new Rectangle() {
    width = 1344
    height = 960
    opacity = 0.0
  }

  val house = new Image(getClass.getResourceAsStream("/gf.game.model/house.png"))
  val houseView: ImageView = new ImageView(house) {
    fitWidth = 393.423
    fitHeight = 543
    x = 722
    y = 335
  }

  val fontStyle = new Font(jfxst.Font.loadFont(getClass.getResourceAsStream("/gf.game.model/NanumPenScript-Regular.ttf"), 25))

  val fontStyle1 = new Font(jfxst.Font.loadFont(getClass.getResourceAsStream("/gf.game.model/DiloWorld.ttf"),50))

  val dayText = new Image(getClass.getResourceAsStream("/gf.game.model/day-text.png"))
  val dayTextView: ImageView = new ImageView(dayText) {
    x = 30
    y = 30
  }

  val playerName: Text = new Text(""){
    font = fontStyle
    fill = Color.White
  }

  val dayCount: Text = new Text("0") {
    font = fontStyle1
    fill = Color.White
    x = 161
    y = 65
  }

  val grave = new Image(getClass.getResourceAsStream("/gf.game.model/grave.png"))
  val graveView: ImageView = new ImageView(grave){
    x = 1
    y = 185
    cursor = Cursor.Hand
  }

  val spout = new Image(getClass.getResourceAsStream("/gf.game.model/spout-base.png"))
  val spoutView: ImageView = new ImageView(spout) {
    x = 490
    y = 264
    cursor = Cursor.Hand
  }

  val spoutShadow = new Image(getClass.getResourceAsStream("/gf.game.model/spout-shadow.png"))
  val spoutShadowView: ImageView = new ImageView(spoutShadow) {
    x = 442
    y = 358
  }

  val spoutOn = new Image(getClass.getResourceAsStream("/gf.game.model/spout-on.png"))
  val spoutOff = new Image(getClass.getResourceAsStream("/gf.game.model/spout-off.png"))
  var spoutStateView: ImageView = new ImageView(spoutOff) {
    x = 530
    y = 246
    cursor = Cursor.Hand
  }

  val backpack = new Image(getClass.getResourceAsStream("/gf.game.model/backpack.png"))
  val backpackView: ImageView = new ImageView(backpack){
    x = 1149
    y = 30
    cursor = Cursor.Hand
  }

  val noteButton = new Image(getClass.getResourceAsStream("/gf.game.model/note-button.png"))
  val noteButtonView: ImageView = new ImageView(noteButton) {
    x = 1119
    y = 804
    cursor = Cursor.Hand
  }

  val note = new Image(getClass.getResourceAsStream("/gf.game.model/note.png"))
  val noteView: ImageView = new ImageView(note) {
    x = 401
    y = 117
  }

  val guideButton = new Image(getClass.getResourceAsStream("/gf.game.model/guide-button.png"))
  val guideButtonView: ImageView = new ImageView(guideButton) {
    x = 1228
    y = 804
    cursor = Cursor.Hand
  }

  val guide = new Image(getClass.getResourceAsStream("/gf.game.model/guide.png"))
  val guideView: ImageView = new ImageView(guide) {
    x = 407
    y = 117
  }

  val sunray = new Image(getClass.getResourceAsStream("/gf.game.model/sunray.png"))
  val sunrayView: ImageView = new ImageView(sunray) {
    x = 429
    y = 0
  }

  //watering can
  val wateringCanShadow = new Image(getClass.getResourceAsStream("/gf.game.model/watering-can-shadow.png"))
  val wateringCanShadowView: ImageView = new ImageView(wateringCanShadow) {
    x = 663
    y = 923
  }

  //soil patches
  val soilPatch = new Image(getClass.getResourceAsStream("/gf.game.model/soil-patch.png"))
  val soilPatch3 = new Image(getClass.getResourceAsStream("/gf.game.model/sp3.png"))
  val sp1: ImageView = new ImageView(soilPatch){
    x = 110
    y = 645
    cursor = Cursor.Hand
  }
  val sp2: ImageView = new ImageView(soilPatch) {
    x = 300
    y = 645
    cursor = Cursor.Hand
  }
  val sp3: ImageView = new ImageView(soilPatch3) {
    x = 490
    y = 645
    cursor = Cursor.Hand
  }
  val sp4: ImageView = new ImageView(soilPatch) {
    x = 110
    y = 790
    cursor = Cursor.Hand
  }
  val sp5: ImageView = new ImageView(soilPatch) {
    x = 300
    y = 790
    cursor = Cursor.Hand
  }
  val sp6: ImageView = new ImageView(soilPatch) {
    x = 490
    y = 790
    cursor = Cursor.Hand
  }

  val inventory = new Image(getClass.getResourceAsStream("/gf.game.model/inventory.png"))
  val inventoryView: ImageView = new ImageView(inventory) {
    x = 286.92
    y = 307.32
  }

  val fertilizerAlert = new Image(getClass.getResourceAsStream("/gf.game.model/fertilizer-alert.png"))
  val fertilizerAlertView = new ImageView(fertilizerAlert)

  val fertilizer = new Image(getClass.getResourceAsStream("/gf.game.model/fertilizer.png"))
  val fertilizerView: ImageView = new ImageView(fertilizer) {
    y = 477
  }

  //seed packs
  val cabbageSeedPack = new Image(getClass.getResourceAsStream("/gf.game.model/cabbage-seed-pack.png"))
  val cabbageSeedPackView: ImageView = new ImageView(cabbageSeedPack) {
    y = 477
  }
  val carrotSeedPack = new Image(getClass.getResourceAsStream("/gf.game.model/carrot-seed-pack.png"))
  val carrotSeedPackView: ImageView = new ImageView(carrotSeedPack) {
    y = 477
  }
  val sunflowerSeedPack = new Image(getClass.getResourceAsStream("/gf.game.model/sunflower-seed-pack.png"))
  val sunflowerSeedPackView: ImageView = new ImageView(sunflowerSeedPack) {
    y = 477
  }

  val wateringCanFull = new Image(getClass.getResourceAsStream("/gf.game.model/watering-can-filled.png"))
  val wateringCanFullView: ImageView = new ImageView(wateringCanFull) {
    y = 479
  }

  //seeds
  val cabbageSeeds = new Image(getClass.getResourceAsStream("/gf.game.model/cabbage-seeds.png"))
  val carrotSeeds = new Image(getClass.getResourceAsStream("/gf.game.model/carrot-seeds.png"))
  val sunflowerSeeds = new Image(getClass.getResourceAsStream("/gf.game.model/sunflower-seeds.png"))
  val cabbageView: ImageView = new ImageView()
  val carrotView: ImageView = new ImageView()
  val sunflowerView: ImageView = new ImageView()
  var chosenSoil: ImageView = new ImageView()   //placeholder

  //sprouts
  val carrotSprout = new Image(getClass.getResourceAsStream("/gf.game.model/carrot-sprout.png"))
  val cabbageSprout = new Image(getClass.getResourceAsStream("/gf.game.model/cabbage-sprout.png"))
  val sunflowerSprout = new Image(getClass.getResourceAsStream("/gf.game.model/sunflower-sprout.png"))

  //medium plants
  val carrotMed = new Image(getClass.getResourceAsStream("/gf.game.model/med-carrot.png"))
  val cabbageMed = new Image(getClass.getResourceAsStream("/gf.game.model/med-cabbage.png"))
  val sunflowerMed = new Image(getClass.getResourceAsStream("/gf.game.model/med-sunflower.png"))

  //grown plants
  val carrotGrown = new Image(getClass.getResourceAsStream("/gf.game.model/carrot.png"))
  val cabbageGrown = new Image(getClass.getResourceAsStream("/gf.game.model/cabbage.png"))
  val sunflowerGrown = new Image(getClass.getResourceAsStream("/gf.game.model/sunflower.png"))

  val seedText1: Text = new Text("carrot seed") {
    font = fontStyle
    fill = Color.web("#916f3f")
    x = 352
    y = 450
  }
  val seedText2: Text = new Text("cabbage seed") {
    font = fontStyle
    fill = Color.web("#916f3f")
    x = 352
    y = 450
  }
  val seedText3: Text = new Text("sunflower seed") {
    font = fontStyle
    fill = Color.web("#916f3f")
    x = 352
    y = 450
  }
  val wateringCanText: Text = new Text("watering can") {
    font = fontStyle
    fill = Color.web("#916f3f")
    x = 352
    y = 450
  }
  val emptyText: Text = new Text("[empty]") {
    font = fontStyle
    fill = Color.web("#916f3f")
    y = 600
  }
  val fullText: Text = new Text("  [full]") {
    font = fontStyle
    fill = Color.web("#916f3f")
    y = 600
  }
  val fertilizerText: Text = new Text("fertilizer") {
    font = fontStyle
    fill = Color.web("#916f3f")
    x = 352
    y = 450
  }

  val wateringCanEmpty = new Image(getClass.getResourceAsStream("/gf.game.model/watering-can-empty.png"))
  val wateringCanEmptyView: ImageView = new ImageView(wateringCanEmpty) {
    x = 662
    y = 873
    cursor = Cursor.Hand
  }

  val soilOverlay = new Image(getClass.getResourceAsStream("/gf.game.model/soil-overlay.png"))
  val waterOverlay = new Image(getClass.getResourceAsStream("/gf.game.model/water-overlay.png"))
  val fertilizerOverlay = new Image(getClass.getResourceAsStream("/gf.game.model/fertilizer-overlay.png"))
  val fertilizerOverlayView: ImageView = new ImageView(fertilizerOverlay)
  val soilOverlayCarrot: ImageView = new ImageView(soilOverlay){cursor = Cursor.Hand}
  val soilOverlayCabbage: ImageView = new ImageView(soilOverlay){cursor = Cursor.Hand}
  val soilOverlaySunflower: ImageView = new ImageView(soilOverlay){cursor = Cursor.Hand}
  val carrotWaterOverlay: ImageView = new ImageView(waterOverlay)
  val cabbageWaterOverlay: ImageView = new ImageView(waterOverlay)
  val sunflowerWaterOverlay: ImageView = new ImageView(waterOverlay)

  var clickableViews: ListBuffer[ImageView] = ListBuffer(graveView, spoutStateView, spoutView, backpackView, noteButtonView, guideButtonView)

  var states: mutable.Map[String, Boolean] = mutable.Map("isFilling" -> false, "isFull" -> false,
    "isPlanting" -> false, "isWatering" -> false, "fertilizerUsed" -> false, "nextDay" -> false)

  var inventoryMap: mutable.LinkedHashMap[String, Text] = mutable.LinkedHashMap(
    "carrotSeedPack" -> seedText1, "cabbageSeedPack" -> seedText2, "sunflowerSeedPack" -> seedText3,
    "wateringCan" -> wateringCanText, "fertilizer" -> fertilizerText
  )
  var inventoryItems: mutable.LinkedHashMap[Text, ImageView] = mutable.LinkedHashMap(
    seedText1 -> carrotSeedPackView, seedText2 -> cabbageSeedPackView, seedText3 -> sunflowerSeedPackView,
    wateringCanText -> wateringCanEmptyView, fertilizerText -> fertilizerView
  )

  var plantSpots: mutable.LinkedHashMap[String, ImageView] = mutable.LinkedHashMap(
    "sp1" -> sp1, "sp2" -> sp2, "sp3" -> sp3,
    "sp4" -> sp4, "sp5" -> sp5, "sp6" -> sp6
  )

  val plantOverlays: Map[String, Map[String,ImageView]] = Map(
    "soilOverlayCarrot" -> Map("soilOverlay" -> soilOverlayCarrot, "waterOverlay" -> carrotWaterOverlay,
      "seedPack" -> carrotSeedPackView, "plantView" -> carrotView),
    "soilOverlayCabbage" -> Map("soilOverlay" -> soilOverlayCabbage, "waterOverlay" -> cabbageWaterOverlay,
      "seedPack" -> cabbageSeedPackView, "plantView" -> cabbageView),
    "soilOverlaySunflower" -> Map("soilOverlay" -> soilOverlaySunflower, "waterOverlay" -> sunflowerWaterOverlay,
      "seedPack" -> sunflowerSeedPackView, "plantView" -> sunflowerView))

  var plantStages: Map[ImageView, Map[String, Image]] = Map(
    carrotView -> Map("seed" -> carrotSeeds, "sprout" -> carrotSprout, "medium" -> carrotMed, "grown" -> carrotGrown),
    cabbageView -> Map("seed" -> cabbageSeeds, "sprout" -> cabbageSprout, "medium" -> cabbageMed, "grown" -> cabbageGrown),
    sunflowerView -> Map("seed" -> sunflowerSeeds, "sprout" -> sunflowerSprout, "medium" -> sunflowerMed, "grown" -> sunflowerGrown),
  )

  val plantGrowthRate: Map[String, Int] = Map(
    "soilOverlayCarrot" -> 3,
    "soilOverlayCabbage" -> 5,
    "soilOverlaySunflower" -> 7,
  )

}
