/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import Database.Database;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.server.UnicastRemoteObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.scene.effect.Glow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.Lighting;
import javafx.scene.effect.MotionBlur;
import javafx.scene.effect.SepiaTone;
import javafx.scene.effect.Shadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 *
 * @author Aliaa Halim, Mohamed El-Alem, Mohamed Shehata, Salma Gaber.
 */
public class TicTacToe extends Application {

    private int coolTitleIndexBegin = 0;
    private int coolTitleIndexEnd = 0;
    private String title = "Login screen";
    private boolean sessionEnded = false;
    private boolean gameRoomFullScreen = false;
    private String currentLoggedUsername;
    private MediaPlayer loginMediaPlayer;
    private MediaPlayer gameRoomMediaPlayer;
    private MediaPlayer gameMediaPlayer;
    private ComputerPlayer computerPlayer;
    private int gameDifficulty;

    private BorderPane gameBorderPane;

    private Scene loginScene;
    private Scene gameRoomScene;
    private Scene registrationFormScene;
    private Scene gameScene;
    private Scene selectDifficultyScene;
    private Scene selectGameModeScene;

    private ListView<String> gameRoomPlayers;

    private RemoteInterface server;
    private RemoteFightInterface player;

    private boolean alreadyChallenged;
    private boolean issuedChallenge;
    private boolean ingame;

    private int game;
    private boolean coopGameTurn;
    private boolean turn;

    private Registry reg;

    private Text gameWin;
    private Text gameLose;
    private Text gameDraw;
    private Text gameCoopPlayer1Win;
    private Text gameCoopPlayer2Win;

    private Text gamePlayerText;
    private Text gameComputerText;

    private Text[][] gameBoardCellsText;

    private GridPane gameCenterGridPane;

    private boolean offlineMode;
    private boolean coopMode;
    private boolean replayMode;
    private boolean userSaveGame;

    private Stage mainStage;

    private Button gameSaveGameButton;

    private String gameMoves;
    private String[] gameMovesInfo;
    private int currentReplayStep;
    
    private TextField loginUsernameField;
    private PasswordField loginPasswordField;
    
    private Button loginSignInButton;
    private Button loginSignUpButton;

    /**
     * Initializing game environment and initiating connection with the server
     */
    
    public TicTacToe() {
        offlineMode = false;
        replayMode = false;
        coopMode = false;
        userSaveGame = false;
        currentLoggedUsername = new String();
        alreadyChallenged = false;
        issuedChallenge = false;
        ingame = false;

        gameComputerText = new Text();
        gamePlayerText = new Text();
        
        loginUsernameField = new TextField();
        loginPasswordField = new PasswordField();
        
        loginSignInButton = new Button("Sign in");

        loginSignUpButton = new Button("Sign up");
        
        loginUsernameField.setVisible(false);
        loginPasswordField.setVisible(false);
        loginSignInButton.setVisible(false);
        loginSignUpButton.setVisible(false);

        new Thread(() -> {
            try {
                reg = LocateRegistry.getRegistry("localhost", 8080);
                server = (RemoteInterface) reg.lookup("server");
                
                loginUsernameField.setVisible(true);
                loginPasswordField.setVisible(true);
                loginSignInButton.setVisible(true);
                loginSignUpButton.setVisible(true);
            } catch (RemoteException | NotBoundException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    @Override
    public void start(Stage primaryStage) {

        mainStage = primaryStage;

        // Creating login Scene
        StackPane loginPane = new StackPane();
        Label loginUserName = new Label("Username");
        Label loginPassword = new Label("Password");

        loginMediaPlayer = new MediaPlayer(new Media(getClass().getResource("/music/Island_Fever.mp3").toString()));
        loginMediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);

        gameRoomMediaPlayer = new MediaPlayer(new Media(getClass().getResource("/music/Whistle_Blower.mp3").toString()));
        gameRoomMediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);

        gameMediaPlayer = new MediaPlayer(new Media(getClass().getResource("/music/lke9c-f08rg.mp3").toString()));
        gameMediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);

        

        Text loginUsernamePasswordWrongText = new Text("Wrong username or password");

        

        Button loginPlayOfflineButton = new Button("Play offline");

        GridPane loginFormGridPane = new GridPane();
        loginFormGridPane.add(loginUserName, 0, 0);
        loginFormGridPane.add(loginUsernameField, 1, 0);
        loginFormGridPane.add(loginPassword, 0, 1);

        loginFormGridPane.add(loginPasswordField, 1, 1);

        loginFormGridPane.add(loginUsernamePasswordWrongText, 1, 2);

        HBox loginButtonHBox = new HBox();
        loginButtonHBox.getChildren().addAll(loginSignInButton, loginSignUpButton);

        loginFormGridPane.add(loginButtonHBox, 1, 3);

        loginPane.getChildren().add(loginFormGridPane);

        BorderPane loginOfflineBorderPane = new BorderPane();
        loginOfflineBorderPane.setRight(loginPlayOfflineButton);

        BorderPane loginBorderPane = new BorderPane();

        loginBorderPane.setTop(loginOfflineBorderPane);
        loginBorderPane.setCenter(loginPane);
        // Login Screen ended

        // Game room Screen
        Button gameRoomSignOutButton = new Button("Sign out");
        Button gameRoomReplayGameButton = new Button("Replay");

        HBox gameRoomSignOutHBox = new HBox();

        gameRoomPlayers = new ListView<>();

        VBox gameRoomTopVBox = new VBox(5);
        gameRoomTopVBox.getChildren().addAll(gameRoomSignOutHBox, gameRoomPlayers);

        Button gameRoomBattleButton = new Button("Battle!");

        HBox gameRoomBottomHBox = new HBox();
        gameRoomBottomHBox.getChildren().add(gameRoomBattleButton);

        gameRoomBattleButton.prefWidthProperty().bind(gameRoomBottomHBox.widthProperty());

        TextArea gameRoomPlayerInfo = new TextArea();

        BorderPane gameRoomPane = new BorderPane();
        gameRoomPane.setRight(gameRoomTopVBox);
        gameRoomPane.setBottom(gameRoomBottomHBox);
        gameRoomPane.setCenter(gameRoomPlayerInfo);

        // Game room ended
        // Registration Form begin
        Label registrationFormFirstNameLabel = new Label("First Name :");
        Label registrationFormLastNameLabel = new Label("Last Name :");
        Label registrationFormUsernameLabel = new Label("User Name :");
        Label registrationFormPasswordLabel = new Label("Password :");
        Label registrationFormConfirmPasswordLabel = new Label("Confirm Password :");

        TextField registrationFormFirstNameTextField = new TextField();

        TextField registrationFormLastNameTextField = new TextField();

        TextField registrationFormUsernameTextField = new TextField();

        PasswordField registrationFormPasswordField = new PasswordField();

        PasswordField registrationFormConfirmPasswordField = new PasswordField();

        Text registrationFormWrongInputText = new Text("Wrong input in one or more fields");

        Text registrationFormUserCreatedText = new Text("User created successfully");

        Button registrationFormSubmitButton = new Button("Submit");
        Button registrationFormClearButton = new Button("Clear");
        Button registrationFormBackButton = new Button("Back to login screen");

        HBox registrationFormButtonsHBox = new HBox(10);

        registrationFormButtonsHBox.getChildren().addAll(registrationFormSubmitButton, registrationFormClearButton);

        GridPane registrationFormPane = new GridPane();
        registrationFormPane.add(registrationFormFirstNameLabel, 0, 0);
        registrationFormPane.add(registrationFormFirstNameTextField, 1, 0);

        registrationFormPane.add(registrationFormLastNameLabel, 0, 1);
        registrationFormPane.add(registrationFormLastNameTextField, 1, 1);

        registrationFormPane.add(registrationFormUsernameLabel, 0, 2);
        registrationFormPane.add(registrationFormUsernameTextField, 1, 2);

        registrationFormPane.add(registrationFormPasswordLabel, 0, 3);
        registrationFormPane.add(registrationFormPasswordField, 1, 3);

        registrationFormPane.add(registrationFormConfirmPasswordLabel, 0, 4);
        registrationFormPane.add(registrationFormConfirmPasswordField, 1, 4);

        registrationFormPane.add(registrationFormWrongInputText, 1, 5);
        registrationFormPane.add(registrationFormUserCreatedText, 1, 5);

        registrationFormPane.add(registrationFormButtonsHBox, 1, 6);

        registrationFormPane.add(registrationFormBackButton, 1, 7);

        // Registration Form end
        // Game begin
        gameBorderPane = new BorderPane();

        BorderPane gameTopBorderPane = new BorderPane();

        gameSaveGameButton = new Button("Save Game");
        gameSaveGameButton.setVisible(false);
        Button gameReplayPrevButton = new Button();
        Button gameReplayNextButton = new Button();

        HBox gameTopCenterHBox = new HBox(50, gameReplayPrevButton, gameReplayNextButton);
        gameTopCenterHBox.setAlignment(Pos.CENTER);

        gameReplayNextButton.setVisible(false);
        gameReplayPrevButton.setVisible(false);

        gameTopBorderPane.setLeft(gamePlayerText);
        gameTopBorderPane.setRight(gameComputerText);
        gameTopBorderPane.setCenter(new StackPane(gameTopCenterHBox, gameSaveGameButton));

        gameBoardCellsText = new Text[3][3];
        gameWin = new Text("You Won");
        gameLose = new Text("You Lose");
        gameDraw = new Text("Tie");
        gameCoopPlayer1Win = new Text("Player 1 Won");
        gameCoopPlayer2Win = new Text("Player 2 Won");

        gameWin.setVisible(false);
        gameLose.setVisible(false);
        gameDraw.setVisible(false);
        gameCoopPlayer1Win.setVisible(false);
        gameCoopPlayer2Win.setVisible(false);

        gameCenterGridPane = new GridPane();
        //gameCenterGridPane.setGridLinesVisible(true);
        gameCenterGridPane.getStyleClass().add("gameCenter"); //sh7ata
        

        //gameCenterGridPane.setStyle("-fx-padding: 10px; -fx-hgap:10 ; -fx-vgap:10;");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                StackPane stackPane = new StackPane();
                stackPane.getStyleClass().add("stack"); //sh7ata
                
                //stackPane.setStyle("-fx-background-color: yellow;"); // deprecated
                gameBoardCellsText[i][j] = new Text();
                gameBoardCellsText[i][j].setFont(Font.font("Beckett", 60));
                stackPane.getChildren().add(gameBoardCellsText[i][j]);
                final int I = i, J = j;
                stackPane.setOnMouseClicked(e -> {
                    boolean gameEnd = false;

                    if (!gameBoardCellsText[I][J].getText().isEmpty()) {
                        return;
                    }
                    if (coopMode) {
                        //stackPane.getStyleClass().remove("stack");
                        //stackPane.getStyleClass().add("stackClicked");
                    
                        if(!coopGameTurn) {
                            
                            gameBoardCellsText[I][J].setText("X");
                            gameBoardCellsText[I][J].setFill(Color.GREEN);
                            computerPlayer.playHumanMove(I, J);
                            
                            if (computerPlayer.checkHumanWin()) {
                                gameBorderPane.setEffect(new Glow(0.6));
                                gameCoopPlayer1Win.setVisible(true);
                                gameEnd = true;
                            }
                        }
                        else {
                            gameBoardCellsText[I][J].setText("O");
                            gameBoardCellsText[I][J].setFill(Color.CHOCOLATE);
                            computerPlayer.playOtherHumanMove(I, J);
                            if(computerPlayer.checkComputerWin()) {
                                computerPlayer.playNextMove();
                                gameBoardCellsText[computerPlayer.getX()][computerPlayer.getY()].setText("O");
                                if (computerPlayer.checkComputerWin()) {
                                    gameBorderPane.setEffect(new Glow(0.6));
                                    gameCoopPlayer2Win.setVisible(true);
                                    gameEnd = true;
                                }
                            }
                        }
                        if (computerPlayer.checkDraw()) {
                            gameCenterGridPane.setEffect(new MotionBlur(160, 20));
                            gameDraw.setVisible(true);
                            gameEnd = true;
                        }
                        
                        if (gameEnd) {
                            new Thread(() -> {
                                try {
                                    Thread.sleep(10000);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                Platform.runLater(() -> {
                                    gameMediaPlayer.stop();
                                    loginMediaPlayer.play();
                                    mainStage.setScene(loginScene);
                                    mainStage.setTitle("Game room window");
                                    gameWin.setVisible(false);
                                    gameDraw.setVisible(false);
                                    gameLose.setVisible(false);
                                    gameCoopPlayer1Win.setVisible(false);
                                    gameCoopPlayer2Win.setVisible(false);
                                    for (int l = 0; l < 3; l++) {
                                        for (int m = 0; m < 3; m++) {
                                            gameBoardCellsText[l][m].setText("");
                                        }
                                    }
                                });
                                setBlurEffectOnBoard(null);
                                gameCenterGridPane.setEffect(null);
                                coopMode = false;

                            }).start();
                        }
                        
                        
                        coopGameTurn = !coopGameTurn;
                    } else if (offlineMode) {
                        gameBoardCellsText[I][J].setText("X");
                        gameBoardCellsText[I][J].setFill(Color.GREEN);
                        //stackPane.getStyleClass().remove("stack");
                        //stackPane.getStyleClass().add("stackClicked");
                    
                        computerPlayer.playHumanMove(I, J);

                        if (computerPlayer.checkHumanWin()) {
                            gameBorderPane.setEffect(new Glow(0.6));
                            gameWin.setVisible(true);
                            gameEnd = true;
                        } else if (computerPlayer.checkDraw()) {
                            gameCenterGridPane.setEffect(new MotionBlur(160, 20));
                            gameDraw.setVisible(true);
                            gameEnd = true;
                        } else {
                            computerPlayer.playNextMove();
                            gameBoardCellsText[computerPlayer.getX()][computerPlayer.getY()].setText("O");
                            gameBoardCellsText[computerPlayer.getX()][computerPlayer.getY()].setFill(Color.CHOCOLATE);
                            if (computerPlayer.checkComputerWin()) {
                                gameBorderPane.setEffect(new InnerShadow(300, Color.BLACK));
                                gameLose.setVisible(true);
                                gameEnd = true;
                            }
                        }

                        if (gameEnd) {
                            new Thread(() -> {
                                try {
                                    Thread.sleep(10000);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                Platform.runLater(() -> {
                                    gameMediaPlayer.stop();
                                    loginMediaPlayer.play();
                                    mainStage.setScene(loginScene);
                                    mainStage.setTitle("Game room window");
                                    gameWin.setVisible(false);
                                    gameDraw.setVisible(false);
                                    gameLose.setVisible(false);
                                    gameCoopPlayer1Win.setVisible(false);
                                    gameCoopPlayer2Win.setVisible(false);
                                    for (int l = 0; l < 3; l++) {
                                        for (int m = 0; m < 3; m++) {
                                            gameBoardCellsText[l][m].setText("");
                                        }
                                    }
                                });
                                setBlurEffectOnBoard(null);
                                gameCenterGridPane.setEffect(null);
                                offlineMode = false;

                            }).start();
                        }
                    } else {
                        if (turn) {
                            //stackPane.getStyleClass().remove("stack");
                            //stackPane.getStyleClass().add("stackClicked");
                    
                            try {
                                server.playMove(I, J, currentLoggedUsername);
                                //stackPane.setStyle("-fx-background-color: red");

                            } catch (RemoteException ex) {
                                Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }

                });

                gameCenterGridPane.add(stackPane, j, i);
            }
        }

        StackPane gameCenterStackPane = new StackPane();
        gameCenterStackPane.getChildren().addAll(gameCenterGridPane, gameWin, gameDraw, gameLose, gameCoopPlayer1Win, gameCoopPlayer2Win);

        gameBorderPane.setCenter(gameCenterStackPane);
        gameBorderPane.setTop(gameTopBorderPane);

        // Game end
        // Select Difficulty Begin
        Button selectDifficultyEasyButton = new Button("Easy");
        Button selectDifficultyMediumButton = new Button("Medium");
        Button selectDifficultyHardButton = new Button("Hard");
        Button selectDifficultyUltimateButton = new Button("Ultimate");

        VBox selectDifficultyVBox = new VBox(30);

        selectDifficultyVBox.getChildren().addAll(selectDifficultyEasyButton, selectDifficultyMediumButton, selectDifficultyHardButton, selectDifficultyUltimateButton);

        StackPane selectDifficultyPane = new StackPane();

        selectDifficultyPane.getChildren().add(selectDifficultyVBox);

        // Select Difficulty End
        // Select game mode
        Button selectSingleModeButton = new Button("1 V.S COM");
        Button selectCoopModeButton = new Button("1 V.S 1");

        VBox selectGameModeVBox = new VBox(30);
        
        selectGameModeVBox.setAlignment(Pos.CENTER);

        selectGameModeVBox.getChildren().addAll(selectSingleModeButton, selectCoopModeButton);

        StackPane selectGameModePane = new StackPane();

        selectGameModePane.getChildren().add(selectGameModeVBox);

        // Select game end
        /*
            All Scenes will be created here below
         */
        loginScene = new Scene(loginBorderPane, 420, 500);
        gameRoomScene = new Scene(gameRoomPane, 700, 700);
        registrationFormScene = new Scene(registrationFormPane, 600, 800);
        gameScene = new Scene(gameBorderPane, 700, 700);
        selectDifficultyScene = new Scene(selectDifficultyPane, 700, 700);
        selectGameModeScene = new Scene(selectGameModePane, 700, 700);

        loginScene.getStylesheets().add(getClass().getResource("styles/style.css").toString());
        registrationFormScene.getStylesheets().add(getClass().getResource("styles/style.css").toString());
        gameRoomScene.getStylesheets().add(getClass().getResource("styles/style.css").toString());
        //gameScene.getStylesheets().add(getClass().getResource("styles/style.css").toString());
        gameScene.getStylesheets().add(getClass().getResource("styles/style.css").toString());
        selectGameModeScene.getStylesheets().add(getClass().getResource("styles/style.css").toString());
        selectDifficultyScene.getStylesheets().add(getClass().getResource("styles/style.css").toString());
        
        gameScene.getStylesheets().add(getClass().getResource("styles/gameStyle.css").toString()); 
        
        

        gameRoomScene.setOnKeyPressed(e -> {
            if (KeyCombination.keyCombination("Ctrl+F").match(e)) {
                gameRoomFullScreen = !gameRoomFullScreen;
                primaryStage.setFullScreen(gameRoomFullScreen);
            } else if (e.getCode() == KeyCode.ESCAPE) {
                primaryStage.setFullScreen(false);
            }
        });

        primaryStage.setScene(loginScene);

        new Thread(() -> {
            while (!sessionEnded) {
                Platform.runLater(() -> {
                    primaryStage.setTitle(title.substring(coolTitleIndexBegin, coolTitleIndexEnd));
                    if (coolTitleIndexEnd < title.length()) {
                        coolTitleIndexEnd++;
                    } else if (coolTitleIndexBegin < coolTitleIndexEnd) {
                        coolTitleIndexBegin++;
                    } else {
                        coolTitleIndexBegin = coolTitleIndexEnd = 0;
                    }

                });
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        });

        primaryStage.setFullScreenExitHint("Press CTRL + F to toggle Full-Screen Mode | ESC to escape Full-Screen Mode");
        primaryStage.show();
        primaryStage.requestFocus();
        
        primaryStage.setTitle("Login Window");
        primaryStage.getIcons().add(new Image(getClass().getResource("/img/ql0bx8cj.png").toString()));

        primaryStage.setMinWidth(420);
        primaryStage.setMinHeight(500);

        loginMediaPlayer.play();

        // Handing login screen
        loginSignInButton.setOnAction(e -> {

            boolean exist = false;
            boolean alreadyLogged = false;
            String username = loginUsernameField.getText().toString();
            try {
                alreadyLogged = server.checkUserLogged(username);
                if (alreadyLogged) {
                    loginUsernamePasswordWrongText.setText("Error user already logged ingame");
                } else {
                    loginUsernamePasswordWrongText.setText("Error login username or password");
                }
                exist = !alreadyLogged && server.checkUserExistence(loginUsernameField.getText(), loginPasswordField.getText());
            } catch (RemoteException | SQLException ex) {
                ex.printStackTrace();
            }
            if (exist) {
                gameRoomFullScreen = true;
                primaryStage.setScene(gameRoomScene);
                //primaryStage.setFullScreen(gameRoomFullScreen);

                currentLoggedUsername = loginUsernameField.getText();

                preformFadeAnimation(gameRoomPane, 1000, 1, false);

                loginUsernameField.setText("");
                loginPasswordField.setText("");
                loginUsernameField.requestFocus();

                try {
                    player = new Player(this);
                    server.addUsername(currentLoggedUsername, player);
                    primaryStage.setTitle(currentLoggedUsername + " Game room");

                    ArrayList<String> users = server.getAllUsers(currentLoggedUsername);
                    for (String user : users) {
                        gameRoomPlayers.getItems().add(user);
                    }
                } catch (RemoteException ex) {
                    Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, ex);
                }

                title = "Game Window";
                coolTitleIndexBegin = coolTitleIndexEnd = 0;
                loginMediaPlayer.stop();
                gameRoomMediaPlayer.play();
            } else {
                preformFadeAnimation(loginUsernamePasswordWrongText, 1000, 6, true);
            }
        });

        loginSignUpButton.setOnAction(e -> {
            primaryStage.setScene(registrationFormScene);
            primaryStage.setTitle("Registration Window");

            preformFadeAnimation(registrationFormPane, 1000, 1, true);

            title = "Registration Screen";
            coolTitleIndexBegin = coolTitleIndexEnd = 0;
            loginUsernameField.setText("");
            loginPasswordField.setText("");
            loginUsernameField.requestFocus();
        });

        loginUsernameField.textProperty().addListener((o, ov, ev) -> {
            if (ev.length() > 0 && (ev.length() > 60 || ev.charAt(ev.length() - 1) == ' ')) {
                loginUsernameField.setText(ov);
            }

            if (loginUsernameField.getText().length() == 0) {
                loginUsernameField.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, new CornerRadii(3), BorderWidths.DEFAULT)));

            } else {
                loginUsernameField.setBorder(Border.EMPTY);
            }
        });

        loginPasswordField.textProperty().addListener((o, ov, ev) -> {
            if (ev.length() > 100) {
                loginPasswordField.setText(ov);
            }

            if (loginPasswordField.getText().length() == 0) {
                loginPasswordField.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, new CornerRadii(3), BorderWidths.DEFAULT)));

            } else {
                loginPasswordField.setBorder(Border.EMPTY);
            }
        });

        loginPlayOfflineButton.setOnAction(e -> {
            //preformFadeAnimation(gameBorderPane, 1000, 1, true);
            primaryStage.setScene(selectGameModeScene);
            primaryStage.setTitle("Select game mode window");
            loginMediaPlayer.stop();
            gameMediaPlayer.play();

            
        });

        loginUserName.setFont(Font.font("Harry Potter", 24));
        loginPassword.setFont(Font.font("Harry Potter", 24));

        loginUsernameField.setFont(Font.font("Avatar"));

        loginPasswordField.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, new CornerRadii(3), BorderWidths.DEFAULT)));

        loginUsernameField.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, new CornerRadii(3), BorderWidths.DEFAULT)));

        loginUsernamePasswordWrongText.setOpacity(0.0);
        loginUsernamePasswordWrongText.setFill(Color.FIREBRICK);
        loginUsernamePasswordWrongText.setFont(Font.font("Borg9"));

        loginSignInButton.setFont(Font.font("Burton's Nightmare", 16));
        loginSignInButton.setDefaultButton(true);

        loginSignUpButton.setFont(Font.font("Burton's Nightmare", 16));

        loginPlayOfflineButton.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(50), BorderWidths.DEFAULT)));
        //loginPlayOfflineButton.setBackground(Background.EMPTY);
        loginPlayOfflineButton.setFont(Font.font("Burton's Nightmare", 20));

        loginButtonHBox.setSpacing(20);

        loginFormGridPane.setHgap(20);
        loginFormGridPane.setVgap(20);

        loginFormGridPane.setAlignment(Pos.CENTER);

        loginBorderPane.setPadding(new Insets(10, 10, 10, 10));

        loginPlayOfflineButton.getStyleClass().add("transparent-button");

        // end of handling
        // Handling Game room
        gameRoomPlayers.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            if (!gameRoomPlayers.getSelectionModel().isEmpty()) {
                try {
                    server.selectUserInfo(gameRoomPlayers.getSelectionModel().getSelectedItem());

                    gameRoomPlayerInfo.setText("Username: " + server.selectUsername() + "\n");
                    gameRoomPlayerInfo.appendText("Name: " + server.selectFirstName() + " " + server.selectLastName() + "\n");
                    gameRoomPlayerInfo.appendText("Number of wins: " + server.selectNumberOfWins() + "\n");
                    gameRoomPlayerInfo.appendText("Number of losses: " + server.selectNumberOfLoss() + "\n");
                    gameRoomPlayerInfo.appendText("Number of Draws: " + server.selectNumberOfDraw());

                } catch (SQLException | RemoteException ex) {
                    Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        });

        gameRoomSignOutButton.setOnAction(e -> {
            try {
                server.removeUsername(currentLoggedUsername, false);
                UnicastRemoteObject.unexportObject(player, true);
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
            currentLoggedUsername = "";
            primaryStage.setScene(loginScene);
            preformFadeAnimation(loginPane, 1000, 1, false);
            title = "Login screen";
            primaryStage.setTitle("Game window");
            coolTitleIndexBegin = coolTitleIndexEnd = 0;
            gameRoomPlayers.getItems().clear();
            gameRoomPlayerInfo.setText("");
            gameRoomMediaPlayer.stop();
            loginMediaPlayer.play();
        });

        gameRoomReplayGameButton.setOnAction(e -> {

            Stage stage = new Stage();
            TextField saveFileName = new TextField();
            saveFileName.setOnAction(ee -> {
                try {
                    gameMoves = server.getGame(currentLoggedUsername, saveFileName.getText());
                    System.out.println(gameMoves);
                    if (gameMoves == null) {
                        Stage errorStage = new Stage();
                        Button Ok = new Button("OK");
                        VBox vbox = new VBox(20, new Text("You don't have a save of that name"), Ok);
                        vbox.setAlignment(Pos.CENTER);
                        Scene scene = new Scene(new StackPane(vbox), 300, 150);

                        Ok.setOnAction(eee -> {
                            errorStage.close();
                        });

                        errorStage.initModality(Modality.APPLICATION_MODAL);
                        errorStage.setScene(scene);

                        errorStage.show();
                    } else {
                        replayMode = true;
                        gameReplayNextButton.setVisible(true);
                        gameReplayPrevButton.setVisible(true);
                        gameRoomMediaPlayer.stop();
                        gameMediaPlayer.play();
                        primaryStage.setScene(gameScene);
                        primaryStage.setTitle("Battle window");
                        gameMovesInfo = gameMoves.split("[\\s]+");
                        gamePlayerText.setText(gameMovesInfo[0]);
                        gameComputerText.setText(gameMovesInfo[1]);
                        currentReplayStep = 2;

                    }
                    stage.close();
                } catch (RemoteException ex) {
                    Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            HBox gameTmpHBox = new HBox(20, new Label("File name: "), saveFileName);
            gameTmpHBox.setAlignment(Pos.CENTER);
            Scene scene = new Scene(new StackPane(gameTmpHBox), 300, 150);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        });

        gameRoomBattleButton.setOnAction(e -> {
            try {
                if (gameRoomPlayers.getSelectionModel().getSelectedItem() != null) {
                    server.challenge(currentLoggedUsername, gameRoomPlayers.getSelectionModel().getSelectedItem());
                    issuedChallenge = true;
                }
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        });

        gameRoomSignOutHBox.setAlignment(Pos.CENTER_RIGHT);
        gameRoomSignOutHBox.getChildren().addAll(gameRoomReplayGameButton, gameRoomSignOutButton);

        gameRoomBattleButton.setFont(Font.font("ARCADE CLASSIC", 24));
        gameRoomBattleButton.setId("battle-button");
        gameRoomSignOutButton.setBackground(Background.EMPTY);
        

        //gameRoomReplayGameButton.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(50), BorderWidths.DEFAULT)));
        gameRoomReplayGameButton.setBackground(Background.EMPTY);
        gameRoomReplayGameButton.setFont(Font.font("Burton's Nightmare", 20));
        
        gameRoomSignOutButton.getStyleClass().add("transparent-button");
        gameRoomReplayGameButton.getStyleClass().add("transparent-button");


        gameRoomPlayerInfo.setEditable(false);

        gameRoomSignOutButton.setFont(Font.font("Burton's Nightmare", 24));

        // End of handling
        // Handling Registration Form
        registrationFormClearButton.setOnAction(e -> {
            registrationFormUsernameTextField.setText("");
            registrationFormPasswordField.setText("");
            registrationFormConfirmPasswordField.setText("");
            registrationFormFirstNameTextField.setText("");
            registrationFormLastNameTextField.setText("");
            registrationFormConfirmPasswordField.setText("");
        });

        registrationFormSubmitButton.setOnAction(e -> {
            try {
                String username = registrationFormUsernameTextField.getText();
                String password = registrationFormPasswordField.getText();
                String doubleCheckPassword = registrationFormConfirmPasswordField.getText();
                String firstName = registrationFormFirstNameTextField.getText();
                String lastName = registrationFormLastNameTextField.getText();

                boolean unique = server.checkUserUniqueness(username);

                if (username.length() > 0 && password.length() > 0 && doubleCheckPassword.length() > 0 && password.equals(doubleCheckPassword) && firstName.length() > 0 && lastName.length() > 0 && unique) {
                    server.addUserToDatabase(username, password, firstName, lastName);
                    preformFadeAnimation(registrationFormUserCreatedText, 1000, 6, true);
                    loginUsernameField.setText(username);
                    loginPasswordField.setText(password);
                } else {

                    preformFadeAnimation(registrationFormWrongInputText, 1000, 6, true);

                    if (!unique || username.length() == 0) {
                        registrationFormUsernameTextField.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, new CornerRadii(3), BorderWidths.DEFAULT)));
                    } else {
                        registrationFormUsernameTextField.setBorder(Border.EMPTY);
                    }

                    if (password.equals(doubleCheckPassword)) {
                        registrationFormPasswordField.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, new CornerRadii(3), BorderWidths.DEFAULT)));
                        registrationFormConfirmPasswordField.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, new CornerRadii(3), BorderWidths.DEFAULT)));
                    } else {
                        registrationFormPasswordField.setBorder(Border.EMPTY);
                        registrationFormConfirmPasswordField.setBorder(Border.EMPTY);
                    }
                }
            } catch (RemoteException | SQLException ex) {
                Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        registrationFormBackButton.setOnAction(e -> {
            primaryStage.setScene(loginScene);
            preformFadeAnimation(loginPane, 1000, 1, false);
            title = "Login screen";
            primaryStage.setTitle("Login window");
            coolTitleIndexBegin = coolTitleIndexEnd = 0;
        });

        registrationFormUsernameTextField.textProperty().addListener((o, ov, ev) -> {
            if (ev.length() > 0 && (ev.length() > 60 || ev.charAt(ev.length() - 1) == ' ')) {
                registrationFormUsernameTextField.setText(ov);
            }

            if (registrationFormUsernameTextField.getText().length() == 0) {
                registrationFormUsernameTextField.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, new CornerRadii(3), BorderWidths.DEFAULT)));

            } else {
                registrationFormUsernameTextField.setBorder(Border.EMPTY);
            }
        });

        registrationFormConfirmPasswordField.textProperty().addListener((o, ov, ev) -> {
            if (ev.length() > 100) {
                registrationFormConfirmPasswordField.setText(ov);
            }

            if (registrationFormConfirmPasswordField.getText().length() == 0) {
                registrationFormConfirmPasswordField.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, new CornerRadii(3), BorderWidths.DEFAULT)));

            } else {
                registrationFormConfirmPasswordField.setBorder(Border.EMPTY);
            }
        });

        registrationFormPasswordField.textProperty().addListener((o, ov, ev) -> {
            if (ev.length() > 100) {
                registrationFormPasswordField.setText(ov);
            }

            if (registrationFormPasswordField.getText().length() == 0) {
                registrationFormPasswordField.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, new CornerRadii(3), BorderWidths.DEFAULT)));

            } else {
                registrationFormPasswordField.setBorder(Border.EMPTY);
            }
        });

        registrationFormFirstNameTextField.textProperty().addListener((o, ov, ev) -> {
            if (ev.length() > 100) {
                registrationFormFirstNameTextField.setText(ov);
            }

            if (registrationFormFirstNameTextField.getText().length() == 0) {
                registrationFormFirstNameTextField.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, new CornerRadii(3), BorderWidths.DEFAULT)));

            } else {
                registrationFormFirstNameTextField.setBorder(Border.EMPTY);
            }
        });

        registrationFormLastNameTextField.textProperty().addListener((o, ov, ev) -> {
            if (ev.length() > 100) {
                registrationFormLastNameTextField.setText(ov);
            }

            if (registrationFormLastNameTextField.getText().length() == 0) {
                registrationFormLastNameTextField.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, new CornerRadii(3), BorderWidths.DEFAULT)));

            } else {
                registrationFormLastNameTextField.setBorder(Border.EMPTY);
            }
        });

        registrationFormFirstNameLabel.setFont(Font.font("Harry Potter", 24));
        registrationFormLastNameLabel.setFont(Font.font("Harry Potter", 24));
        registrationFormUsernameLabel.setFont(Font.font("Harry Potter", 24));
        registrationFormPasswordLabel.setFont(Font.font("Harry Potter", 24));
        registrationFormConfirmPasswordLabel.setFont(Font.font("Harry Potter", 24));

        registrationFormFirstNameTextField.setFont(Font.font("Avatar"));
        registrationFormLastNameTextField.setFont(Font.font("Avatar"));
        registrationFormUsernameTextField.setFont(Font.font("Avatar"));

        registrationFormSubmitButton.setFont(Font.font("Burton's Nightmare", 16));
        registrationFormClearButton.setFont(Font.font("Burton's Nightmare", 16));
        registrationFormBackButton.setFont(Font.font("Burton's Nightmare", 24));

        registrationFormPane.setAlignment(Pos.CENTER);
        registrationFormPane.setHgap(10);
        registrationFormPane.setVgap(20);

        registrationFormConfirmPasswordField.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, new CornerRadii(3), BorderWidths.DEFAULT)));
        registrationFormPasswordField.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, new CornerRadii(3), BorderWidths.DEFAULT)));
        registrationFormUsernameTextField.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, new CornerRadii(3), BorderWidths.DEFAULT)));
        registrationFormFirstNameTextField.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, new CornerRadii(3), BorderWidths.DEFAULT)));
        registrationFormLastNameTextField.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, new CornerRadii(3), BorderWidths.DEFAULT)));

        registrationFormWrongInputText.setOpacity(0.0);
        registrationFormWrongInputText.setFill(Color.FIREBRICK);
        registrationFormWrongInputText.setFont(Font.font("Borg9"));

        registrationFormUserCreatedText.setOpacity(0.0);
        registrationFormUserCreatedText.setFill(Color.valueOf("#0BDB19"));
        registrationFormUserCreatedText.setFont(Font.font("Borg9"));

        // End handling
        // Handling game
        gameBorderPane.setPadding(new Insets(5));

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPercentWidth(100 / 3.0);

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setPercentHeight(100 / 3.0);

        gameCenterGridPane.getRowConstraints().addAll(rowConstraints, rowConstraints, rowConstraints);
        gameCenterGridPane.getColumnConstraints().addAll(columnConstraints, columnConstraints, columnConstraints);

        gameWin.setFont(Font.font("Beast Wars", 72));
        gameWin.setFill(Color.FORESTGREEN);

        gameDraw.setFont(Font.font("Beast Wars", 72));
        gameDraw.setFill(Color.ORANGERED);

        gameLose.setFont(Font.font("Beast Wars", 72));
        gameLose.setFill(Color.CHOCOLATE);
        
        gameCoopPlayer1Win.setFont(Font.font("Beast Wars", 48));
        gameCoopPlayer1Win.setFill(Color.FORESTGREEN);
        
        gameCoopPlayer2Win.setFont(Font.font("Beast Wars", 48));
        gameCoopPlayer2Win.setFill(Color.FORESTGREEN);

        gamePlayerText.setFont(Font.font("Aladdin", 60));
        gamePlayerText.setFill(Color.LAWNGREEN);

        gameComputerText.setFont(Font.font("Aladdin", 60));
        gameComputerText.setFill(Color.PALEVIOLETRED);

        gameSaveGameButton.setFont(Font.font("ARCADE CLASSIC", 24));

        gameSaveGameButton.setOnAction(e -> {
            userSaveGame = true;
            System.out.println("Save clicked");
        });

        gameReplayNextButton.setOnAction(e -> {

            if (currentReplayStep < gameMovesInfo.length - 1) {
                String info[] = gameMovesInfo[currentReplayStep].split(",");
                System.out.println(Arrays.toString(gameMovesInfo));
                gameBoardCellsText[Integer.parseInt(info[0])][Integer.parseInt(info[1])].setText(info[2]);
                if(info[2].equals("X")) {
                    gameBoardCellsText[Integer.parseInt(info[0])][Integer.parseInt(info[1])].setFill(Color.GREEN);
                            
                }
                else {
                    gameBoardCellsText[Integer.parseInt(info[0])][Integer.parseInt(info[1])].setFill(Color.CHOCOLATE);

                }
                currentReplayStep++;
            } else if (currentReplayStep == gameMovesInfo.length - 1) {
                if (gameMovesInfo[currentReplayStep].equals("win")) {
                    gameWin.setVisible(true);
                    setBlurEffectOnBoard(new Glow(0.6));
                } else if (gameMovesInfo[currentReplayStep].equals("draw")) {
                    gameDraw.setVisible(true);
                    gameCenterGridPane.setEffect(new MotionBlur(160, 20));
                } else {
                    gameLose.setVisible(true);
                    setBlurEffectOnBoard(new InnerShadow(300, Color.BLACK));
                }
                currentReplayStep++;
            } else {
                gameMediaPlayer.stop();
                gameRoomMediaPlayer.play();
                primaryStage.setScene(gameRoomScene);

                gameWin.setVisible(false);
                gameDraw.setVisible(false);
                gameLose.setVisible(false);
                gameReplayNextButton.setVisible(false);
                gameReplayPrevButton.setVisible(false);
                
                setBlurEffectOnBoard(null);
                gameCenterGridPane.setEffect(null);
                
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        gameBoardCellsText[i][j].setText("");
                    }
                }
                replayMode = false;
            }

        });

        gameReplayPrevButton.setOnAction(e -> {
            System.out.println("There");
            if (currentReplayStep == gameMovesInfo.length) {
                currentReplayStep--;
            }
            if (currentReplayStep > 2) {
                currentReplayStep--;
                String info[] = gameMovesInfo[currentReplayStep].split(",");
                System.out.println(Arrays.toString(info));
                gameBoardCellsText[Integer.parseInt(info[0])][Integer.parseInt(info[1])].setText("");
                gameWin.setVisible(false);
                gameDraw.setVisible(false);
                gameLose.setVisible(false);
                gameCenterGridPane.setEffect(null);
                setBlurEffectOnBoard(null);
            }

        });
        
        
        ImageView gameReplayNextImageButton = new ImageView(new Image(getClass().getResource("/img/Oxygen-Icons_org-Oxygen-Actions-arrow-right-0.png").toString()));
        ImageView gameReplayPrevImageButton = new ImageView(new Image(getClass().getResource("/img/Oxygen-Icons_org-Oxygen-Actions-arrow-left-0.png").toString()));
        
        gameReplayNextImageButton.setFitHeight(20);
        gameReplayNextImageButton.setFitWidth(30);
        
        gameReplayPrevImageButton.setFitHeight(20);
        gameReplayPrevImageButton.setFitWidth(30);
        
        gameReplayNextButton.setGraphic(gameReplayNextImageButton);        
        gameReplayPrevButton.setGraphic(gameReplayPrevImageButton);


        // End handling
        // Handling Select Difficulty
        selectDifficultyEasyButton.setOnAction(e -> {
            gameDifficulty = 1;
            computerPlayer = new ComputerPlayer(gameDifficulty);
            primaryStage.setScene(gameScene);
            primaryStage.setTitle("Battle window");

        });

        selectDifficultyMediumButton.setOnAction(e -> {
            gameDifficulty = 3;
            computerPlayer = new ComputerPlayer(gameDifficulty);
            primaryStage.setScene(gameScene);
             primaryStage.setTitle("Battle window");

        });

        selectDifficultyHardButton.setOnAction(e -> {
            gameDifficulty = 5;
            computerPlayer = new ComputerPlayer(gameDifficulty);
            primaryStage.setScene(gameScene);
            primaryStage.setTitle("Battle window");

        });

        selectDifficultyUltimateButton.setOnAction(e -> {
            gameDifficulty = 10;
            computerPlayer = new ComputerPlayer(gameDifficulty);
            primaryStage.setScene(gameScene);
            primaryStage.setTitle("Battle window");

        });

        selectDifficultyVBox.setAlignment(Pos.CENTER);

        selectDifficultyEasyButton.setFont(Font.font("Blade 2", 60));
        selectDifficultyMediumButton.setFont(Font.font("Blade 2", 60));
        selectDifficultyHardButton.setFont(Font.font("Blade 2", 60));
        selectDifficultyUltimateButton.setFont(Font.font("Blade 2", 60));

        //End handling
        // Handling select mode
        
        selectSingleModeButton.setFont(Font.font("Blade 2", 60));
        selectCoopModeButton.setFont(Font.font("Blade 2", 60));
        
        selectSingleModeButton.setOnAction(e -> {
            offlineMode = true;
            primaryStage.setScene(selectDifficultyScene);
            loginMediaPlayer.stop();
            gameMediaPlayer.play();
            gamePlayerText.setText("Human");
            gameComputerText.setText("Computer");
            primaryStage.setTitle("Select difficulty window");
            
        });

        selectCoopModeButton.setOnAction(e -> {
            coopMode = true;
            coopGameTurn = false;
            computerPlayer = new ComputerPlayer(10);
            primaryStage.setScene(gameScene);
            primaryStage.setTitle("Battle window");
            
            gamePlayerText.setText("Player 1");
            gameComputerText.setText("Player 2");
        });

        // End handling
        // Handing Game Scene
        // End handling
    }

    public void preformFadeAnimation(Node node, long millis, int count, boolean reversable) {
        FadeTransition ft = new FadeTransition();
        ft.setNode(node);
        ft.setDuration(Duration.millis(millis));
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.setCycleCount(count);
        ft.setAutoReverse(reversable);
        ft.play();
    }

    public void playOnBoard(int x, int y, String turn) {
        Platform.runLater(() -> {
            gameBoardCellsText[x][y].setText(turn);
            if(turn.equals("X")) {
                gameBoardCellsText[x][y].setFill(Color.GREEN);
            } else {
                gameBoardCellsText[x][y].setFill(Color.CHOCOLATE);

            }
        });
        gameMoves += " " + Integer.toString(x) + "," + Integer.toString(y) + "," + turn;
    }
    
    /**
     * Add certain effect to the game
     * @param effect effect applied to the board
     */

    public void setBlurEffectOnBoard(Effect effect) {
        Platform.runLater(() -> {
            gameBorderPane.setEffect(effect);
        });

    }
    
    
    /**
     * Message from the server that tells this user that he won his game
     */

    public void setGameWin() {
        try {
            server.increaseWin(currentLoggedUsername);
        } catch (RemoteException ex) {
            Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, ex);
        }

        Platform.runLater(() -> {
            gameWin.setVisible(true);
        });
        setBlurEffectOnBoard(new Glow(0.6));
        waitingGameEndThread(1);

    }

    /**
     * Message from the server that tells this user that his game finished withdraw
     */
    
    public void setGameDraw() {
        try {
            server.increaseDraw(currentLoggedUsername);
        } catch (RemoteException ex) {
            Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, ex);
        }

        gameCenterGridPane.setEffect(new MotionBlur(160, 20));

        Platform.runLater(() -> {
            gameDraw.setVisible(true);
        });
        waitingGameEndThread(2);
    }
    
    /**
     * Message from the server that tells this user that he lost his game
     */

    public void setGameLose() {
        try {
            server.increaseLoss(currentLoggedUsername);
        } catch (RemoteException ex) {
            Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, ex);
        }

        setBlurEffectOnBoard(new InnerShadow(300, Color.BLACK));

        Platform.runLater(() -> {
            gameLose.setVisible(true);
        });

        waitingGameEndThread(3);

    }
    
    
    /**
     * Just do some the effect with 10 seconds delay
     * @param status game status(win = 1, draw = 2, lose = 3)
     */

    public void waitingGameEndThread(int status) {
        if (status == 1) {
            gameMoves += " " + "win";
        } else if (status == 2) {
            gameMoves += " " + "draw";
        } else {
            gameMoves += " " + "lose";
        }
        new Thread(() -> {
            try {
                Thread.sleep(10000);
                server.endGame(currentLoggedUsername);
                //server.addUsername(currentLoggedUsername, player);
                System.out.println("Adding " + currentLoggedUsername + " To the menu");
            } catch (InterruptedException ex) {
                Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, ex);
            } catch (RemoteException ex) {
                Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, ex);
            }
            Platform.runLater(() -> {
                gameMediaPlayer.stop();
                gameRoomMediaPlayer.play();
                mainStage.setScene(gameRoomScene);
                mainStage.setTitle("Game room window");
                gameWin.setVisible(false);
                gameDraw.setVisible(false);
                gameLose.setVisible(false);
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        gameBoardCellsText[i][j].setText("");
                    }
                }
            });
            setBlurEffectOnBoard(null);
            gameCenterGridPane.setEffect(null);
            gameSaveGameButton.setVisible(false);
            if (userSaveGame) {
                userSaveGame = false;
                Platform.runLater(() -> {

                    Stage stage = new Stage();
                    TextField fileName = new TextField();
                    fileName.setOnAction(e -> {
                        try {
                            server.saveGame(currentLoggedUsername, gameMoves, fileName.getText());
                            stage.close();
                        } catch (RemoteException ex) {
                            Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                    HBox saveGameHBox = new HBox(20, new Label("File name: "), fileName);
                    saveGameHBox.setAlignment(Pos.CENTER);
                    Scene scene = new Scene(new StackPane(saveGameHBox), 300, 150);
                    stage.setScene(scene);
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.show();
                });
            }
        }).start();
    }
    
    /**
     * Handling closing the game
     */

    @Override
    public void stop() {
        sessionEnded = true;
        if (!currentLoggedUsername.isEmpty()) {
            try {
                server.removeUsername(currentLoggedUsername, false);
                UnicastRemoteObject.unexportObject(player, true);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Add the newly added username to the game menu
     * @param username the player username 
     */

    public void addUserToMenu(String username) {
        Platform.runLater(() -> {
            gameRoomPlayers.getItems().add(username);
        });
    }
    
    /**
     * Remove a player from current user game room due to logging out or participating in game
     * @param username the player username
     */

    public void removeUserFromMenu(String username) {
        Platform.runLater(() -> {
            gameRoomPlayers.getItems().remove(username);
        });
    }
    
    /**
     * 
     * @return the primary stage 
     */

    Stage getStage() {
        return mainStage;
    }
    
    /**
     * Recieves a challenge from another user
     * @param username 
     */

    void checkChallenge(String username) {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            Text text = new Text(username + " Challenges you");

            Button acceptChallenge = new Button("Accept");
            Button denyChallenge = new Button("Deny");

            HBox hbox = new HBox(20);
            hbox.getChildren().addAll(acceptChallenge, denyChallenge);
            hbox.setAlignment(Pos.CENTER);

            VBox vbox = new VBox(20);
            vbox.getChildren().addAll(text, hbox);

            vbox.setAlignment(Pos.CENTER);

            Scene scene = new Scene(new StackPane(vbox), 300, 150);
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();

            acceptChallenge.setOnAction(e -> {
                try {
                    server.challengeStatus(username, currentLoggedUsername, true);
                    stage.close();
                } catch (RemoteException ex) {
                    Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            denyChallenge.setOnAction(e -> {
                try {
                    server.challengeStatus(username, currentLoggedUsername, false);
                    stage.close();
                } catch (RemoteException ex) {
                    Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

        });
    }
    
    /**
     * sets the turn of this player
     * @param turn the choice of the server to this player's turn 
     */

    public void setTurn(boolean turn) {
        this.turn = turn;
    }
    
    /**
     * flips the current turn
     */

    public void flipTurn() {
        turn = !turn;
    }
    
    /**
     * Opens a multiplayer game
     * @param player1 username of player1
     * @param player2 username of player2
     */

    public void openGame(String player1, String player2) {
        Platform.runLater(() -> {
            preformFadeAnimation(gameBorderPane, 1000, 1, false);
            mainStage.setScene(gameScene);
            gameRoomMediaPlayer.stop();
            gameMediaPlayer.play();
            gamePlayerText.setText(player1);
            gameComputerText.setText(player2);
            gameSaveGameButton.setVisible(true);
            gameMoves = new String(player1 + " " + player2);
            mainStage.setTitle("Battle window");
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    /**
     * get the current username
     * @return current username
     */

    public String getCurrentUserName() {
        return currentLoggedUsername;
    }
    
    /**
     * Close connection incase of other player disconnection
     */

    public void terminate() {
        Platform.runLater(() -> {
            gameMediaPlayer.stop();
            gameRoomMediaPlayer.play();

            mainStage.setScene(gameRoomScene);

            Stage stage = new Stage();
            Button Ok = new Button("OK");
            VBox vbox = new VBox(20, new Text("Player disconnected"), Ok);
            vbox.setAlignment(Pos.CENTER);
            Scene scene = new Scene(new StackPane(vbox), 300, 150);

            Ok.setOnAction(e -> {
                stage.close();
            });

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);

            stage.show();
        });
    }
    
    /**
     * shows a dialog if the other player refused the challenge
     */

    public void challengeDenied() {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            Button Ok = new Button("OK");
            VBox vbox = new VBox(20, new Text("Your challenge has been refused"), Ok);
            vbox.setAlignment(Pos.CENTER);
            Scene scene = new Scene(new StackPane(vbox), 300, 100);

            Ok.setOnAction(e -> {
                stage.close();
            });

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);

            stage.show();
        });
    }
    
}
