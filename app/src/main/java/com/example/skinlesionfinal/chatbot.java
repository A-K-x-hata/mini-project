package com.example.skinlesionfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;

public class chatbot extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView welcomeTextView;
    private EditText messageEditText;
    private ImageButton sendButton;
    private List<Message> messageList;
    private MessageAdapter messageAdapter;
    private GenerativeModel generativeModel = new GenerativeModel("gemini-pro", "AIzaSyCSZsQlFTeey7GcGhCe0EeMCdbbccWG2K4");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        messageList = new ArrayList<>();

        recyclerView = findViewById(R.id.r_v);
        welcomeTextView = findViewById(R.id.welcome_text);
        messageEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_button);

        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        sendButton.setOnClickListener(v -> {
            String question = messageEditText.getText().toString().trim();
            if (!question.isEmpty()) {
                addToChat(question, Message.SENT_BY_ME);
                messageEditText.setText("");
                callAPI(question);
                welcomeTextView.setVisibility(View.GONE);
            }
        });
    }

    private void addToChat(String message, String sentBy) {
        runOnUiThread(() -> {
            messageList.add(new Message(message, sentBy));
            messageAdapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
        });
    }

    private void addResponse(String response) {
        runOnUiThread(() -> {
            messageList.remove(messageList.size() - 1);
            addToChat(response, Message.SENT_BY_BOT);
        });
    }

    private void callAPI(String question) {
        messageList.add(new Message("Typing...", Message.SENT_BY_BOT));

        GenerativeModelFutures model = GenerativeModelFutures.from(generativeModel);
        Content content = new Content.Builder().addText(question).build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    String resultText = result.getText();
                    addResponse(resultText.trim());
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    Log.e("ChatGPT", "Failed to load response", t);
                    addResponse("Failed to load response due to " + t.getMessage());
                }
            }, getMainExecutor());
        }
    }
}