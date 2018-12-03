package inc.brody.ship;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;

public class GameScreen extends ScreenAdapter {
    private static final float WIDTH = 1280;
    private static final float HEIGHT = 720;

    private static final float WIDTH_FIELD = 560;
    private static final float WIDTH_CELL = WIDTH_FIELD/10;

    private int[][] cellsArray;
    private int[][] myCellsArray;

    private boolean isMyTurn = true;
    private boolean isJustClicked = false;
    private boolean isMyCreated;
    private boolean isShoot = false;
    private boolean isCreated;
    private boolean isHetted = false;
    private int isWon = -1;

    private Label Slabel;

    private ArrayList<Cell> cells;
    private ArrayList<EnemyCell> enemyCells;


    private Game game;
    private OrthographicCamera camera;
    private Table table,tableMine,backTable,backTableMine;

    private Stage stage;

    public GameScreen(Game game){
        this.game = game;

        camera = new OrthographicCamera(WIDTH,HEIGHT);
        camera.position.set(camera.viewportWidth/2,camera.viewportHeight/2,0);
        camera.update();

        stage = new Stage(new FitViewport(camera.viewportWidth,camera.viewportHeight));

        cells = new ArrayList<Cell>();
        enemyCells = new ArrayList<EnemyCell>();

        table = new Table();
        tableMine = new Table();

        table.setFillParent(true);

        backTable = new Table();
        backTableMine = new Table();
        backTableMine.setFillParent(true);
        backTable.setFillParent(true);

        cellsArray = new int[10][10];
        myCellsArray = new int[10][10];


        cellsArray = spawnShipsRandomly();
        isCreated = true;
        myCellsArray = spawnShipsRandomly();

        initButtons();
        initEnemyCells();
        initTable(table,backTable,cellsArray);
        isMyCreated = true;
        initTable(tableMine,backTableMine,myCellsArray);
        tableMine.addAction(Actions.moveBy(0,HEIGHT));
        backTableMine.addAction(Actions.moveBy(0,HEIGHT));
        initRight();
        initLabelTurn();
        initInputProcessor();
    }

    private void initRight() {
        Label x = new Label("x",new Label.LabelStyle(Assets.font,Color.BLACK));
        x.setBounds(WIDTH-70,HEIGHT-70,70,70);
        x.setAlignment(Align.center);
        x.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Gdx.app.exit();
            }
        });

        Label warship = new Label("Battle\n Ship",new Label.LabelStyle(Assets.font,Color.BLACK));
        warship.setBounds(WIDTH - (WIDTH-WIDTH_FIELD)/2,0,(WIDTH-WIDTH_FIELD)/2,HEIGHT);
        warship.setAlignment(Align.center);

        stage.addActor(warship);
        stage.addActor(x);
    }

    private void initLabelTurn() {
        Slabel = new Label("Now it's \nyour\n turn",new Label.LabelStyle(Assets.font,Color.BLACK));
        Container<Label> labels = new Container<Label>(Slabel);
        labels.setBounds(0,0,(WIDTH - WIDTH_FIELD)/2,HEIGHT);
        Slabel.setAlignment(Align.center);
        Image white = new Image(Assets.white);
        white.setBounds(0,0,WIDTH,HEIGHT);
        white.addAction(Actions.sequence(
                Actions.fadeOut(.5f,Interpolation.sine),
                Actions.moveBy(-10000,-10000)
        ));
        labels.addAction(Actions.forever(
                Actions.sequence(
                        Actions.scaleBy(50f,50f,.5f,Interpolation.sine),
                        Actions.scaleBy(-50f,-50f,.5f,Interpolation.sine)
                )
        ));

        stage.addActor(labels);
        stage.addActor(white);
    }

    private void enemyTurn() {
            if(!isMyTurn){
                isJustClicked = false;
                Slabel.setText("Now it's \nbot's\n turn");

                stage.addAction(Actions.sequence(
                        Actions.delay(.5f),
                        new Action() {
                            @Override
                            public boolean act(float delta) {
                                while (!isShoot) {
                                    int randomShoot = MathUtils.random(0, 99);
                                    if (myCellsArray[randomShoot / 10][randomShoot % 10] != -1 && myCellsArray[randomShoot / 10][randomShoot % 10] != -2) {
                                        isShoot = true;
                                        initClick(randomShoot);
                                    }
                                }
                                isShoot = false;
                                return true;
                            }
                        },
                        Actions.delay(1),
                        new Action() {
                            @Override
                            public boolean act(float delta) {
                                isMyTurn = true;
                                if(!isHetted) {
                                    tableMine.addAction(Actions.moveBy(0,HEIGHT,.5f,Interpolation.sine));
                                    backTableMine.addAction(Actions.moveBy(0,HEIGHT,.5f,Interpolation.sine));
                                    table.addAction(Actions.moveBy(0,HEIGHT,.5f,Interpolation.sine));
                                    backTable.addAction(Actions.moveBy(0,HEIGHT,.5f,Interpolation.sine));
                                    Slabel.setText("Now it's\n your \nturn");
                                }
                                return true;
                            }
                        },
                        Actions.delay(.5f),
                        new Action() {
                            @Override
                            public boolean act(float delta) {
                                if(isHetted){
                                    isHetted = false;
                                    isMyTurn = false;
                                    isJustClicked = true;
                                    enemyTurn();
                                }

                                return true;
                            }
                        }
                ));


        }
    }


    private int[][] spawnShipsRandomly() {
        int[][] tempCellsArray = new int[10][10];
        int[] amount = {4,3,2,1};
        int randomPlace;
        for(int i = 0; i < 10; i++){
            for(int j = 0; j < 10; j++){
                tempCellsArray[i][j] = 0;
            }
        }

        int[] coords = {0,0};
        for (int i = 3; i >= 0; i--){
            while (amount[i] > 0){
                int size = i+1;
                if(size == 1){
                    boolean isDone = false;
                    while (!isDone){
                        isDone = true;
                        randomPlace = MathUtils.random(0,99);
                        coords[0] = randomPlace/10;
                        coords[1] = randomPlace%10;
                        for(int q = -1; q <= 1; q++){
                            for(int a = -1; a <= 1; a++){
                                if(checkBounds(coords,q,a)){
                                    if(tempCellsArray[coords[0] + q][coords[1]+a] == 1){
                                        isDone = false;
                                    }
                                }
                            }
                        }
                        if(isDone){
                            tempCellsArray[coords[0]][coords[1]] = 1;
                        }
                    }
                } else {
                    boolean isDone = false;

                    while (!isDone){
                        int col = MathUtils.random(0,10 - size);
                        int row = MathUtils.random(0,9);
                        coords[0] = col;
                        coords[1] = row;
                        isDone = true;
                        double angle = MathUtils.random(0,1);

                        if(angle >= .5){
                            for(int q = -1; q <= i+1; q++){
                                for(int a = -1; a <= 1; a++){
                                    if(coords[0]+q>=0 && coords[0]+q < 10 && coords[1]+a >=0 && coords[1]+a < 10){
                                        if(tempCellsArray[coords[0] + q][coords[1]+a] == 1){
                                            isDone = false;
                                        }
                                    }
                                }
                            }
                            if(isDone) {
                                for(int q = i; q >= 0; q--){
                                    tempCellsArray[coords[0]+q][coords[1]] = 1;
                                }
                            }
                        } else {
                            coords[0] = row;
                            coords[1] = col;

                            for(int q = -1; q <= 1; q++){
                                for(int a = -1; a <= i+1; a++){
                                    if(coords[0]+q>=0 && coords[0]+q < 10 && coords[1]+a >=0 && coords[1]+a < 10){
                                        if(tempCellsArray[coords[0] + q][coords[1]+a] == 1){
                                            isDone = false;
                                        }
                                    }

                                }
                            }
                            if(isDone){
                                for(int q = i; q >= 0; q--){
                                    tempCellsArray[coords[0]][coords[1]+q] = 1;
                                }
                            }
                        }

                    }

                }

                amount[i]--;
            }
        }
        for (int i = 0; i < 10; i++){
            for(int j = 0; j < 10; j++){
                System.out.print(tempCellsArray[i][j]);
            }
            System.out.println();
        }

        return initArray(tempCellsArray,!isCreated?cellsArray:myCellsArray);
    }

    private void initEnemyCells() {
        for(int i = 0; i < 100; i++){
            enemyCells.add(new EnemyCell(new int[]{i/10,i%10}));
            stage.addActor(enemyCells.get(i));
        }
    }

    private void initButtons() {
        for(int i = 0; i < 100; i++){
            cells.add(new Cell(new int[]{i/10,i%10}));
            final int finalI = i;
            cells.get(i).addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    if(!cells.get(finalI).isTouched){
                        if(!isJustClicked && isMyTurn){
                            isJustClicked = true;
                            initClick(finalI);
                            stage.addAction(new Action() {
                                        @Override
                                        public boolean act(float delta) {
                                            isMyTurn = false;
                                            if(!isHetted){
                                                stage.addAction(Actions.sequence(
                                                        Actions.delay(1f),
                                                        new Action() {
                                                            @Override
                                                            public boolean act(float delta) {
                                                                tableMine.addAction(Actions.moveBy(0,-HEIGHT,.5f,Interpolation.sine));
                                                                backTableMine.addAction(Actions.moveBy(0,-HEIGHT,.5f,Interpolation.sine));
                                                                table.addAction(Actions.moveBy(0,-HEIGHT,.5f,Interpolation.sine));
                                                                backTable.addAction(Actions.moveBy(0,-HEIGHT,.5f,Interpolation.sine));
                                                                enemyTurn();
                                                                return  true;
                                                            }
                                                        }
                                                ));
                                            } else {
                                                isJustClicked = false;
                                                isMyTurn = true;
                                                isHetted = false;
                                            }
                                            return true;
                                        }
                                    }
                            );
                        }
                    }

                }
            });
            stage.addActor(cells.get(i));
        }
    }

    private void initClick(final int finalI){
        int[][] prevCells = new int[10][10];
        int[][] nowCells = new int[10][10];
        boolean isTurn = isJustClicked;
        prevCells = initArray(isTurn ?cellsArray:myCellsArray,prevCells);
        nowCells = initArray(prevCells,nowCells);

        int[] gotCells;
        if(isTurn) gotCells = cells.get(finalI).coords;
        else gotCells = enemyCells.get(finalI).coords;

        if(nowCells[gotCells[0]][gotCells[1]] == 1) {
            isHetted = true;
            ArrayList<Integer[]> checkMore = new ArrayList<Integer[]>();
            checkMore.add(new Integer[]{gotCells[0],gotCells[1]});
            boolean isLast = true;
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if(checkBounds(gotCells,i,j)){
                        if (nowCells[gotCells[0] + i][gotCells[1]+j] == 1){
                            isLast = false;
                        } else if(nowCells[gotCells[0] + i][gotCells[1] + j] == -2){
                            checkMore.add(new Integer[]{gotCells[0]+i,gotCells[1]+j});
                            checkMore = checkForward(checkMore);
                            if(checkMore.get(checkMore.size()-1)[0] == 11){
                                isLast = false;
                            }
                        }
                    }
                }
            }
            if(isLast){
                for (Integer[] integers: checkMore){
                    int[] temp = {integers[0],integers[1]};
                    for(int i = -1; i <= 1; i++){
                        for(int j = -1; j <= 1; j++){
                            if(checkBounds(temp,i,j)) {
                                if (nowCells[temp[0] + i][temp[1] + j] != -2) {
                                    nowCells[temp[0] + i][temp[1] + j] = -1;
                                    if(isTurn) cells.get((temp[0]+i)*10+temp[1]+j).isTouched = true;
                                }
                            }
                            if(i== 0 && j == 0){
                                nowCells[temp[0]][temp[1]] = -2;
                                if(isTurn) cells.get((temp[0]+i)*10+temp[1]+j).isTouched = true;
                            }
                        }
                    }
                }
            } else {
                nowCells[gotCells[0]][gotCells[1]] = -2;
                if(isTurn) cells.get((gotCells[0])*10+gotCells[1]).isTouched = true;
            }


        } else if(nowCells[gotCells[0]][gotCells[1]] == 0){
            nowCells[gotCells[0]][gotCells[1]] = -1;
            if(isTurn) cells.get((gotCells[0])*10+gotCells[1]).isTouched = true;
        }

        for (int i = 0; i < 10; i++){
            for(int j = 0; j < 10; j++){
                System.out.print(nowCells[i][j]);
            }
            System.out.println();
        }

        for (int i = 0; i < 10; i++){
            for(int j = 0; j < 10; j++){
                if(prevCells[i][j] != nowCells[i][j]){
                    //(isMyTurn ? cells : enemyCells).get(i*10+j).destroyCell();
                    if(isTurn){
                        cells.get(i*10+j).destroyCell();
                    }
                    else {
                        enemyCells.get(i*10+j).destroyCell();
                    }
                }
            }
        }

        for(int i = 0; i < 10;i++){
            for(int j = 0; j < 10; j++){
                if(isTurn) cellsArray[i][j] = nowCells[i][j];
                else myCellsArray[i][j] = nowCells[i][j];
            }
        }

        boolean isEnd = false;
        boolean isEndEnemy = false;
        for(int i = 0; i < 10;i++){
            for(int j = 0; j < 10; j++){
                if(cellsArray[i][j] == 1) isEndEnemy = true;
                if(myCellsArray[i][j] == 1) isEnd = true;
            }
        }

        if(!isEndEnemy) isWon = 1;
        if(!isEnd) isWon = 0;

        if(isWon > -1) initGameOver(isWon);

    }

    private void initGameOver(int isWon) {
        System.out.println("INITGAMEOVER");
        Label label = new Label("",new Label.LabelStyle(Assets.font,Color.BLACK));
        label.setFontScale(1.5f,1.5f);
        label.setBounds(-10000,-10000,WIDTH,HEIGHT);
        label.setWrap(true);
        label.setAlignment(Align.center);
        if(isWon == 1) label.setText("You won!");
        else if(isWon == 0) label.setText("You lost!");
        label.getColor().a = 0;
        Image image = new Image(Assets.white);
        image.setBounds(-10000,-10000,WIDTH,HEIGHT);
        image.getColor().a = 0;
        image.addAction(Actions.parallel(
                Actions.alpha(.95f,.5f,Interpolation.sine),
                Actions.moveTo(0,0)
        ));
        label.addAction(Actions.parallel(
                Actions.fadeIn(.5f,Interpolation.sine),
                Actions.moveTo(0,100)
        ));
        Image restart = new Image(Assets.restart);
        restart.setBounds(-10000,-10000,80,80);
        restart.getColor().a = 0;
        restart.addAction(Actions.parallel(
                Actions.fadeIn(.5f,Interpolation.sine),
                Actions.moveTo(WIDTH/2 - 40,HEIGHT/2 - 80)
        ));
        restart.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                game.setScreen(new GameScreen(game));
            }
        });

        stage.addActor(image);
        stage.addActor(restart);
        stage.addActor(label);

    }

    private int[][] initArray(int[][] nowCells, int[][] ints) {
        for(int i = 0; i < 10;i++){
            for(int j = 0; j < 10; j++){
                ints[i][j] = nowCells[i][j];
            }
        }
        return ints;
    }

    private ArrayList<Integer[]> checkForward(ArrayList<Integer[]> checkMore) {
        int temp[] = {checkMore.get(checkMore.size()-1)[0],checkMore.get(checkMore.size()-1)[1]};
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if(checkBounds(new int[]{temp[0],temp[1]},i,j)) {
                    if (cellsArray[temp[0]+ i][temp[1]+j] == -2){
                            if(checkMore.size() > 2) {
                                if (!(checkMore.get(checkMore.size() - 2)[0] == (temp[0] + i) && checkMore.get(checkMore.size() - 2)[1] == (temp[1] + j))) {
                                    checkMore.add(new Integer[]{checkMore.get(checkMore.size() - 1)[0] + i, checkMore.get(checkMore.size() - 1)[1] + j});
                                    checkForward(checkMore);
                                }
                            } else {
                                if (!(checkMore.get(checkMore.size() - 2)[0] == (temp[0] + i) && checkMore.get(checkMore.size() - 2)[1] == (temp[1] + j))) {
                                    checkMore.add(new Integer[]{checkMore.get(checkMore.size() - 1)[0] + i, checkMore.get(checkMore.size() - 1)[1] + j});
                                    checkForward(checkMore);
                                }
                            }
                    }
                    if(cellsArray[temp[0]+ i][temp[1]+j] == 1) {
                                if (!(checkMore.get(0)[0] == (temp[0] + i) && checkMore.get(0)[1] == (temp[1] + j))) {
                                    checkMore.add(new Integer[]{11,0});
                                    return checkMore;
                                }
                    }
                }
            }
        }
        return checkMore;
    }

    private boolean checkBounds(int[] checkCoords,int i,int j) {
        return checkCoords[0]+i>=0 && checkCoords[0]+i < 10 && checkCoords[1]+j >=0 && checkCoords[1]+j < 10 && !(i==0 && j==0);
    }

    private void initTable(Table table,Table backTable, int[][] cellsar) {
        for(int i = 0; i < 10; i++){
            for(int j = 0; j < 10; j++){
                if(cellsar[i][j] == 1){
                    backTable.add(new Image(Assets.xmark)).width(WIDTH_CELL).height(WIDTH_CELL);
                } else {
                    backTable.add(new Image(Assets.zero)).width(WIDTH_CELL).height(WIDTH_CELL);
                }
                table.add((!isMyCreated ? cells : enemyCells).get(i*10+j)).width(WIDTH_CELL).height(WIDTH_CELL);
            }
            backTable.row();
            table.row();
        }
        stage.addActor(backTable);
        stage.addActor(table);
    }

    private void initInputProcessor(){
        InputMultiplexer im = new InputMultiplexer();
        im.addProcessor(stage);
        im.addProcessor(new InputAdapter(){
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {

                return false;
            }
        });
        Gdx.input.setInputProcessor(im);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Gdx.gl.glClearColor(255f,255f,255f,0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));

        stage.act();
        stage.draw();
    }
}
