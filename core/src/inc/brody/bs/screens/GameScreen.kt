package inc.brody.bs.screens

import com.badlogic.gdx.*
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.FitViewport
import inc.brody.bs.entities.game.Cell
import inc.brody.bs.entities.game.EnemyCell
import inc.brody.bs.entities.logical.CustomAction
import inc.brody.bs.entities.logical.FullscreenImage
import inc.brody.bs.entities.logical.LastLabel
import inc.brody.bs.functions.*
import inc.brody.bs.objects.Assets
import inc.brody.bs.objects.Constants
import inc.brody.bs.objects.Constants.HEIGHT
import inc.brody.bs.objects.Constants.WIDTH_CELL
import inc.brody.bs.objects.Won


class GameScreen(private val game: Game) : ScreenAdapter() {
    private var isCreated = false
    private var isMyCreated = false
    private var isMyTurn = true
    private var isJustClicked = false
    private var isShoot = false
    private var isHit = false
    private var isWon = Won.DEFAULT

    //Initiate default stuff
    private var mCamera: Camera = OrthographicCamera(Constants.WIDTH, Constants.HEIGHT)
    private var mStage: Stage
    private lateinit var mLabel: LastLabel

    //Create Battlefield
    private val tableEnemy = Table()
    private val tableMine = Table()
    private val backEnemy = Table()
    private val backMine = Table()

    //Arrays of entities
    private val cells = arrayListOf<Cell>()
    private val enemyCells = arrayListOf<EnemyCell>()

    //Arrays of entities' numbers
    private val enemyCellsArray = spawnShipsRandomly()
    private val mineCellsArray = spawnShipsRandomly()


    //Initiation of main game logic
    init {
        mCamera.apply {
            position.set(mCamera.viewportWidth / 2, mCamera.viewportHeight / 2, 0f)
            update()
        }

        mStage = Stage(FitViewport(mCamera.viewportWidth, mCamera.viewportHeight))
        setTableFillParent(tableEnemy, backMine, backEnemy, tableMine)

        initButtons()
        initEnemyCells()
        isCreated = true


        initTable(tableEnemy, backEnemy, enemyCellsArray)
        isMyCreated = true
        initTable(tableMine, backMine, mineCellsArray)

        moveTables(tableMine, backMine,tableEnemy,backEnemy)

        initAdditionalScreen()
        initLabelTurn()
        initInputProcessor(mStage)
    }

    //Adding non-essential graphics at the screen
    private fun initAdditionalScreen() {
        val x = LastLabel("x").setLastScreenLabel(Constants.LABEL_X_POS, Constants.HEIGHT - Constants.LABEL_X_SIZE,
                Constants.LABEL_X_SIZE, Constants.LABEL_X_SIZE) { Gdx.app.exit() }
        val warship = LastLabel("Battle\n Ship").setLastScreenLabel(Constants.LABEL_WARSHIP_POS, 0f,
                Constants.LABEL_WARSHIP_WIDTH, Constants.HEIGHT)

        mStage.setActors(warship,x)
    }


    //Adding label which shows one's turn
    private fun initLabelTurn() {
        mLabel = LastLabel("Now it's \nyour turn").setLastScreenLabel()
        val labels = Container<Label>(mLabel)
        val white = FullscreenImage(Assets.white)

        white.addAction(CustomAction.moveAwayAlphaAction())
        labels.addAction(CustomAction.foreverScalingAction(50f))

        labels.setBounds(0f, 0f, Constants.LABEL_WARSHIP_WIDTH, Constants.HEIGHT)

        mStage.setActors(labels, white)
    }

    //Random arrangement of ships in the beginning of the game
    private fun spawnShipsRandomly(): Array<IntArray> {
        val tempCellsArray = zeroes
        val amount = intArrayOf(4, 3, 2, 1)
        var size: Int
        var isDone: Boolean
        var isAngle: Boolean
        val coords = intArrayOf(0, 0)

        for (i in 3 downTo 0) {
            while (amount[i] > 0) {
                size = i + 1
                isDone = false
                while (!isDone) {
                    isAngle = MathUtils.random(0f, 1f) >= .5f
                    coords[0] = MathUtils.random(0, if (isAngle) 10 - size else 9)
                    coords[1] = MathUtils.random(0, if (isAngle) 9 else 10 - size)
                    isDone = true
                    for (q in -1..i + 1) {
                        for (a in -1..1) {
                            if (checkBounds(coords, if (isAngle) q else a, if (isAngle) a else q)) {
                                if (tempCellsArray[coords[0] + if (isAngle) q else a][coords[1] + if (isAngle) a else q] == 1) {
                                    isDone = false
                                }
                            }
                        }
                    }
                    if (isDone) {
                        for (q in i downTo 0) {
                            tempCellsArray[coords[0] + if (isAngle) q else 0][coords[1] + if (isAngle) 0 else q] = 1
                        }
                    }
                }
                amount[i]--
            }
        }
        return tempCellsArray
    }


    //Initiation of cells, which are buttons and will be clicked by player during the game
    private fun initButtons() {
        for (i in 0 until 100) {
            cells.add(Cell(intArrayOf(i / 10, i % 10)))
            cells[i].addListener {
                if (!cells[i].isTouched && !isJustClicked && isMyTurn) {
                    isJustClicked = true
                    initClick(i)
                    mStage.setOwnAction {
                        isMyTurn = false
                        if (!isHit) {
                            mStage.addAction(CustomAction.sequenceDelayOwnAction(1f) {
                                moveTables(tableEnemy, tableMine, backEnemy, backMine, height = -Constants.HEIGHT, duration = .5f)
                                enemyTurn()
                                true
                            })
                        } else {
                            isJustClicked = false
                            isMyTurn = true
                            isHit = false
                        }
                        true
                    }
                }
                true
            }
            mStage.addActor(cells[i])
        }
    }


    //Creating new array
    private fun initArray(nowCells: Array<IntArray>): Array<IntArray> {
        val tempCellsArray = zeroes

        funcForToTen { i, j ->
            tempCellsArray[i][j] = nowCells[i][j]
        }
        return tempCellsArray
    }


    //Handling one's click
    private fun initClick(i: Int) {
        val prevCells = initArray(if (isJustClicked) enemyCellsArray else mineCellsArray)
        val nowCells = initArray(prevCells)
        val gotCells = if (isJustClicked) cells[i].coords else enemyCells[i].coords

        if (nowCells[gotCells[0]][gotCells[1]] == 1) {
            var isLast = true
            var checkMore = ArrayList<IntArray>()
            isHit = true
            checkMore.add(intArrayOf(gotCells[0], gotCells[1]))
            funcForInBlock { u, j ->
                if (checkClickBounds(gotCells, u, j)) {
                    if (nowCells[gotCells[0] + u][gotCells[1] + j] == 1) {
                        isLast = false
                    } else if (nowCells[gotCells[0] + u][gotCells[1] + j] == -2) {
                        checkMore.add(intArrayOf(gotCells[0] + u, gotCells[1] + j))
                        checkMore = checkForward(checkMore)
                        if (checkMore[checkMore.size - 1][0] == 11) isLast = false
                    }
                }
            }
            if (isLast) {
                for (int in checkMore) {
                    val temp = intArrayOf(int[0], int[1])
                    funcForInBlock { u, j ->
                        if (checkClickBounds(temp, u, j)) {
                            if (nowCells[temp[0] + u][temp[1] + j] != -2) {
                                nowCells[temp[0] + u][temp[1] + j] = -1
                                checkIsTouch(temp, u, j)
                            }
                        }
                        if (u == 0 && j == 0) {
                            nowCells[temp[0]][temp[1]] = -2
                            checkIsTouch(temp, u, j)
                        }
                    }
                }
            } else {
                nowCells[gotCells[0]][gotCells[1]] = -2
                checkIsTouch(gotCells, 0, 0)
            }

        } else if (nowCells[gotCells[0]][gotCells[1]] == 0) {
            nowCells[gotCells[0]][gotCells[1]] = -1
            checkIsTouch(gotCells, 0, 0)
        }


        funcForToTen { u, j ->
            if (prevCells[u][j] != nowCells[u][j]) {
                if (isJustClicked) cells[u * 10 + j].destroyCell()
                else enemyCells[u * 10 + j].destroyCell()
            }
        }

        funcForToTen { u, j ->
            if (isJustClicked) enemyCellsArray[u][j] = nowCells[u][j]
            else mineCellsArray[u][j] = nowCells[u][j]
        }

        var isEnd = false
        var isEndEnemy = false
        funcForToTen { u, j ->
            if (enemyCellsArray[u][j] == 1) isEndEnemy = true
            if (mineCellsArray[u][j] == 1) isEnd = true
        }

        if (!isEndEnemy) isWon = Won.VICTORY
        if (!isEnd) isWon = Won.LOST

        if (isWon != Won.DEFAULT) initGameOver(isWon)
    }


    //Checking if currently clicked ship is totally destroyed or just damaged
    private fun checkForward(checkMore: ArrayList<IntArray>): ArrayList<IntArray> {
        val temp = intArrayOf(checkMore[checkMore.size - 1][0], checkMore[checkMore.size - 1][1])
        funcForInBlock { i, j ->
                if (checkClickBounds(intArrayOf(temp[0], temp[1]), i, j)) {
                    if ((if(!isMyTurn )mineCellsArray else enemyCellsArray)[temp[0] + i][temp[1] + j] == -2) {
                        if (!(checkMore[checkMore.size - 2][0] == (temp[0] + i) && checkMore[checkMore.size - 2][1] == (temp[1] + j))) {
                            checkMore.add(intArrayOf(checkMore[checkMore.size - 1][0] + i, checkMore[checkMore.size - 1][1] + j))
                            checkForward(checkMore)
                        }
                    }
                    if ((if(!isMyTurn )mineCellsArray else enemyCellsArray)[temp[0] + i][temp[1] + j] == 1) {
                        if (!(checkMore[0][0] == (temp[0] + i) && checkMore[0][1] == (temp[1] + j))) {
                            checkMore.add(intArrayOf(11, 0))
                            return@funcForInBlock
                        }
                    }
            }
        }
        return checkMore
    }


    //The hit of stupid random-shooting bot :) (Configure it as you wish)
    private fun enemyTurn() {
        if (!isMyTurn) {
            isJustClicked = false
            mLabel.setText("Now it's \nbot's\n turn")
            mStage.addAction(CustomAction.enemyTurnAction(.5f, 1f, .5f, firstAction = {
                while (!isShoot) {
                    val randomShoot = MathUtils.random(0, 99)
                    if (mineCellsArray[randomShoot / 10][randomShoot % 10] != -1 && mineCellsArray[randomShoot / 10][randomShoot % 10] != -2) {
                        isShoot = true
                        initClick(randomShoot)
                    }
                }
                isShoot = false
            }, secondAction = {
                isMyTurn = true
                if(!isHit){
                    moveTables(backMine, backEnemy, tableMine, tableEnemy,height = HEIGHT,duration = .5f)
                    mLabel.setText("Now it's\n your \nturn")
                }
            }, thirdAction = {
                if (isHit) {
                    isHit = false
                    isMyTurn = false
                    isJustClicked = true
                    enemyTurn()
                }
            }))
        }
    }


    //Showing the game over screen
    private fun initGameOver(isWon: Won) {
        val label = LastLabel(if (isWon == Won.VICTORY) "You won!" else "You lost!")
                .setLastScreenLabel(Constants.GETAWAY, Constants.GETAWAY, Constants.WIDTH,
                        Constants.HEIGHT, isFontScale = 1.5f, isWrap = true, alpha = 0f)

        val image = FullscreenImage(Assets.white, Constants.GETAWAY, Constants.GETAWAY, alpha = 0f)

        val restart = FullscreenImage(Assets.restart, Constants.GETAWAY, Constants.GETAWAY,
                Constants.RESTART_SIZE, Constants.RESTART_SIZE, alpha = 0f) {
            game.screen = GameScreen(game)
        }

        image.addAction(CustomAction.parallelFading(0f, 0f, .95f))
        label.addAction(CustomAction.parallelFading(0f, 100f))
        restart.addAction(CustomAction.parallelFading(Constants.WIDTH / 2 - Constants.RESTART_SIZE / 2,
                Constants.HEIGHT / 2 - Constants.RESTART_SIZE / 2))

        mStage.setActors(image, label, restart)
    }


    //Drawing and initiating battle field tables
    private fun initTable(table: Table, backTable: Table, cellsar: Array<IntArray>) {
        for (i in 0 until 10) {
            for (j in 0 until 10) {
                if (cellsar[i][j] == 1)
                    backTable.add(Image(Assets.xmark)).setMeasure(WIDTH_CELL)
                else
                    backTable.add(Image(Assets.zero)).setMeasure(WIDTH_CELL)
                table.add((if (!isMyCreated) cells else enemyCells)[i * 10 + j]).setMeasure(WIDTH_CELL)
            }
            rowTables(backTable,table)
        }
        if(!isMyCreated) moveTables(backTable,table,height = -Constants.HEIGHT,duration = 0f)
        mStage.setActors(backTable, table)
    }


    //Drawing enemy's static field
    private fun initEnemyCells() {
        for (i in 0 until 100) {
            enemyCells.add(EnemyCell(intArrayOf(i / 10, i % 10)))
            mStage.addActor(enemyCells[i])
        }
    }

    private fun checkIsTouch(array: IntArray, i: Int = 0, j: Int = 0) {
        if (isJustClicked) cells[(array[0] + i) * 10 + array[1] + j].isTouched = true
    }

    override fun render(delta: Float) {
        super.render(delta)
        Gdx.gl.glClearColor(255f, 255f, 255f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT or if (Gdx.graphics.bufferFormat.coverageSampling) GL20.GL_COVERAGE_BUFFER_BIT_NV else 0)

        mStage.act()
        mStage.draw()
    }
}