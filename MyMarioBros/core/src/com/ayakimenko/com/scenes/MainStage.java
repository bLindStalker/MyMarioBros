package com.ayakimenko.com.scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import static com.ayakimenko.com.tools.utils.Constants.V_HEIGHT;
import static com.ayakimenko.com.tools.utils.Constants.V_WIDTH;

public class MainStage implements Disposable {
    private static Label scoreLabel;
    private static Integer score = 0;

    public Stage stage;

    private Label countDownLabel;
    private Integer worldTimer = 300;
    private float timeCount = 0;

    public MainStage(SpriteBatch sb) {

        Viewport viewport = new FillViewport(V_WIDTH, V_HEIGHT);
        stage = new Stage(viewport, sb);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        Label.LabelStyle style = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        Label marioLabel = new Label("MARIO", style);
        table.add(marioLabel).expandX().padTop(10);

        Label worldLabel = new Label("WORLD", style);
        table.add(worldLabel).expandX().padTop(10);

        Label timeLabel = new Label("TIME", style);
        table.add(timeLabel).expandX().padTop(10);

        table.row();
        scoreLabel = new Label(score.toString(), style);
        table.add(scoreLabel).expandX();

        Label levelLabel = new Label("1-1", style);
        table.add(levelLabel).expandX();

        countDownLabel = new Label(worldTimer.toString(), style);
        table.add(countDownLabel).expandX();

        stage.addActor(table);
    }

    public static void addScore(int value) {
        score += value;
        scoreLabel.setText(score.toString());
    }

    public void update(float dl) {
        timeCount += dl;
        if (timeCount >= 1) {
            worldTimer--;
            countDownLabel.setText(worldTimer.toString());
            timeCount = 0;
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
