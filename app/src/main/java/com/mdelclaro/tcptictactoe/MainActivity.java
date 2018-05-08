package com.mdelclaro.tcptictactoe;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // 0: microsoft, 1: linux, 2: empty

    TcpClient tcpClient;

    ImageView slot;

    int activePlayer = 0, tappedSlot;

    boolean gameActive = true;

    int[] gameState = {2, 2, 2, 2, 2, 2, 2, 2, 2};

    int[][] winningPositions = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, {0, 4, 8}, {2, 4, 6}};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new ConnectTask().execute("");

        Log.i("INFO", "tcpClient.run");

        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                //

            }
        }, 2000);

    }

    public void dropIn(View view) {

        slot = (ImageView) view;

        tappedSlot = Integer.parseInt(slot.getTag().toString());

        if (tcpClient != null) {

            String msg = slot.getTag().toString();

            msg = msg.substring(0, msg.length());

            tcpClient.sendMessage(msg);

            Log.i("INFO", "Slot Sent: " + msg);

        } else {

            Log.i("INFO", "TcpClient null");
        }

    }

    public void checkState(){

        for (int[] winningPosition : winningPositions) {

            if (gameState[winningPosition[0]] == gameState[winningPosition[1]] && gameState[winningPosition[1]] == gameState[winningPosition[2]] && gameState[winningPosition[0]] != 2) {

                gameActive = false;

                String winner;

                if (activePlayer == 1) {

                    winner = "Windows Ganhou!";

                } else {

                    winner = "Linux Ganhou!";

                }

                winner = winner.substring(0, winner.length());

                if (tcpClient != null) {

                    tcpClient.sendMessage(winner);

                    Log.i("INFO", "Sent: " + winner);

                } else {

                    Log.i("INFO", "TcpClient null");
                }

                Button playAgainButton = findViewById(R.id.playAgainButton);

                TextView winnerTextView = findViewById(R.id.winnerTextView);

                winnerTextView.setText(winner);

                playAgainButton.setVisibility(View.VISIBLE);

                winnerTextView.setVisibility(View.VISIBLE);

                return;

            }

            if (gameActive &&
                    gameState[0] != 2 &&
                    gameState [1] != 2 &&
                    gameState [2] != 2 &&
                    gameState [3] != 2 &&
                    gameState [4] != 2 &&
                    gameState [5] != 2 &&
                    gameState [6] != 2 &&
                    gameState [7] != 2 &&
                    gameState [8] != 2) {

                for (int[] winningPosition2 : winningPositions) {

                    if (gameState[winningPosition2[0]] == gameState[winningPosition2[1]] && gameState[winningPosition2[1]] == gameState[winningPosition2[2]] && gameState[winningPosition2[0]] != 2) {

                        gameActive = false;

                        String winner;

                        if (activePlayer == 1) {

                            winner = "Windows Ganhou!";

                        } else {

                            winner = "Linux Ganhou!";

                        }

                        winner = winner.substring(0, winner.length());

                        if (tcpClient != null) {

                            tcpClient.sendMessage(winner);

                            Log.i("INFO", "Sent: " + winner);

                        } else {

                            Log.i("INFO", "TcpClient null");
                        }

                        Button playAgainButton = findViewById(R.id.playAgainButton);

                        TextView winnerTextView = findViewById(R.id.winnerTextView);

                        winnerTextView.setText(winner);

                        playAgainButton.setVisibility(View.VISIBLE);

                        winnerTextView.setVisibility(View.VISIBLE);

                        return;

                    }
                }

                String winner = "Empate!";

                if (tcpClient != null) {

                    winner = winner.substring(0, winner.length());

                    tcpClient.sendMessage(winner);

                    Log.i("INFO", "Sent EMPATE");

                } else {

                    Log.i("INFO", "TcpClient null");
                }

                Button playAgainButton = findViewById(R.id.playAgainButton);

                TextView winnerTextView = findViewById(R.id.winnerTextView);

                winnerTextView.setText(winner);

                playAgainButton.setVisibility(View.VISIBLE);

                winnerTextView.setVisibility(View.VISIBLE);

                return;

            }

        }

    }

    public void playAgain(View view) {

        Button playAgainButton = findViewById(R.id.playAgainButton);

        TextView winnerTextView = findViewById(R.id.winnerTextView);

        playAgainButton.setVisibility(View.INVISIBLE);

        winnerTextView.setVisibility(View.INVISIBLE);

        GridLayout gridLayout = findViewById(R.id.gridLayout);

        for(int i = 0; i < gridLayout.getChildCount(); i++) {

            ImageView slot = (ImageView) gridLayout.getChildAt(i);

            slot.setImageDrawable(null);

        }

        activePlayer = 0;

        gameActive = true;

        for(int i = 0; i < gameState.length; i++) {

            gameState[i] = 2;

        }

        if (tcpClient != null) {

            String msg = "Jogo Reiniciado";
            msg = msg.substring(0, msg.length());

            tcpClient.sendMessage(msg);

            Log.i("INFO", "Sent: " + msg);

        } else {

            Log.i("INFO", "TcpClient null");
        }

    }

    public class ConnectTask extends AsyncTask<String, String, TcpClient> {

        @Override
        protected TcpClient doInBackground(String... strings) {

            tcpClient = new TcpClient(new TcpClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            });

            tcpClient.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            Log.i("INFO", "RESPONSE: " + values[0]);

            String imageView = "imageView" + values[0];
            int resID = getResources().getIdentifier(imageView, "id", getPackageName());

            ImageView slot = findViewById(resID);

            Log.i("DEBUG", imageView);

            if (gameState[Integer.parseInt(values[0])] == 2 && gameActive) {

                gameState[Integer.parseInt(values[0])] = activePlayer;

                slot.setTranslationY(-1500);

                if (activePlayer == 0) {

                    slot.setImageResource(R.drawable.windows);

                    activePlayer = 1;

                } else {

                    slot.setImageResource(R.drawable.linux);

                    activePlayer = 0;

                }

                slot.animate().translationYBy(1500).setDuration(300);

                checkState();

            }
        }
    }

}
