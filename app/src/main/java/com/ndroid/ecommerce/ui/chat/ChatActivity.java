package com.ndroid.ecommerce.ui.chat;

import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ndroid.ecommerce.R;
import com.ndroid.ecommerce.adapter.chatbot.ChatAdapter;
import com.ndroid.ecommerce.chatbot.ChatMessage;
import com.ndroid.ecommerce.chatbot.ChatbotDatabase;
import com.ndroid.ecommerce.chatbot.ChatbotEngine;
import com.ndroid.ecommerce.ui.UserDashboardActivity;
import com.ndroid.ecommerce.ui.order.ShoppingCartActivity;
import com.ndroid.ecommerce.ui.product.HomeActivity;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private EditText messageEditText;
    private ImageButton sendButton;
    private LinearLayout runCtrlContainer;
    
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private ChatbotEngine chatbotEngine;
    private ChatbotDatabase chatbotDatabase;
    private int currentSessionId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        
        // Kiểm tra xem layout đã được load đúng chưa
        Log.d("MainActivity", "Layout loaded: " + R.layout.activity_chat);
        
        // Lấy session_id từ intent nếu có
        if (getIntent().hasExtra("session_id")) {
            currentSessionId = getIntent().getIntExtra("session_id", 1);
        }
        
        // Khởi tạo các thành phần UI
        initializeUI();
    }
    
    private void initializeUI() {
        // Khởi tạo views
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        runCtrlContainer = findViewById(R.id.runCtrlContainer);

        // Thiết lập bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_chat);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Intent intent = new Intent(ChatActivity.this, HomeActivity.class);
                intent.setFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            } else if (id == R.id.nav_chat) {
                return true;
            } else if (id == R.id.nav_shop) {
                Intent intent2 = new Intent(ChatActivity.this, ShoppingCartActivity.class);
                intent2.setFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent2);
            } else if (id == R.id.nav_profile) {
                Intent intent3 = new Intent(ChatActivity.this, UserDashboardActivity.class);
                intent3.setFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent3);
            }
            return true;
        });
        
        // Khởi tạo chatbot engine và database
        chatbotEngine = new ChatbotEngine(this);
        chatbotDatabase = new ChatbotDatabase(this);
        
        // Lấy lịch sử chat từ database
        chatMessages = chatbotDatabase.getAllMessages(currentSessionId);
        if (chatMessages == null) {
            chatMessages = new ArrayList<>();
        }
        
        // Thiết lập RecyclerView
        chatAdapter = new ChatAdapter(chatMessages);
        chatRecyclerView.setAdapter(chatAdapter);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Thiết lập sự kiện click cho nút gửi
        sendButton.setOnClickListener(v -> sendMessage());
        
        // Thiết lập sự kiện click cho các nút action
        setupActionButtons();

        // Thêm tin nhắn chào mừng nếu không có lịch sử chat
        if (chatMessages.isEmpty()) {
            String welcomeMessage = "Xin chào! Tôi là Omen, trợ lý ảo của bạn. Tôi có thể giúp gì cho bạn?";
            chatbotDatabase.addMessage(welcomeMessage, "bot", currentSessionId);
            chatMessages.add(new ChatMessage(welcomeMessage, "bot"));
            chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_chat);
    }

    @Override
    protected void onStop() {
        super.onStop();
        overridePendingTransition(0, 0);
    }

    private void setupActionButtons() {
        // Tìm các nút action
        View chatButton = findViewById(R.id.actionChat);
        View newChatButton = findViewById(R.id.actionNewChat);
        View historyButton = findViewById(R.id.actionHistory);
        
        // Thiết lập sự kiện click
        if (chatButton != null) {
            chatButton.setOnClickListener(v -> {
                // Đã ở trang chat, không cần làm gì
            });
        }
        
        if (newChatButton != null) {
            newChatButton.setOnClickListener(v -> createNewChat());
        }
        
        if (historyButton != null) {
            historyButton.setOnClickListener(v -> openChatHistory());
        }
    }

    private void createNewChat() {
        // Lưu tiêu đề cho session hiện tại dựa trên tin nhắn đầu tiên của người dùng
        if (!chatMessages.isEmpty()) {
            String title = "";
            for (ChatMessage message : chatMessages) {
                if ("user".equals(message.getSender())) {
                    title = message.getMessage();
                    break;
                }
            }
            
            if (title.isEmpty()) {
                title = "Chat mới";
            } else if (title.length() > 20) {
                // Cắt tiêu đề nếu quá dài và thêm dấu "..."
                title = title.substring(0, 20) + "...";
            }
            
            chatbotDatabase.updateSessionTitle(currentSessionId, title);
        }
        
        // Tạo session mới
        currentSessionId = chatbotDatabase.createNewSession("Chat mới");
        
        // Xóa tin nhắn hiện tại
        chatMessages.clear();
        chatAdapter.notifyDataSetChanged();
        
        // Thêm tin nhắn chào mừng
        String welcomeMessage = "Xin chào! Tôi là Omen, trợ lý ảo của bạn. Tôi có thể giúp gì cho bạn?";
        chatbotDatabase.addMessage(welcomeMessage, "bot", currentSessionId);
        chatMessages.add(new ChatMessage(welcomeMessage, "bot"));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
    }

    private void openChatHistory() {
        // Lưu tiêu đề cho session hiện tại trước khi chuyển sang màn hình lịch sử
        if (!chatMessages.isEmpty()) {
            String title = "";
            for (ChatMessage message : chatMessages) {
                if ("user".equals(message.getSender())) {
                    title = message.getMessage();
                    break;
                }
            }
            
            if (title.isEmpty()) {
                title = "Chat mới";
            } else if (title.length() > 20) {
                // Cắt tiêu đề nếu quá dài và thêm dấu "..."
                title = title.substring(0, 20) + "...";
            }
            
            chatbotDatabase.updateSessionTitle(currentSessionId, title);
        }
        
        // Mở màn hình lịch sử chat
        Intent intent = new Intent(this, ChatHistoryActivity.class);
        startActivity(intent);
    }

    private void sendMessage() {
        String messageText = messageEditText.getText().toString().trim();
        if (!messageText.isEmpty()) {
            // Lưu tin nhắn người dùng
            chatbotDatabase.addMessage(messageText, "user", currentSessionId);
            ChatMessage userMessage = new ChatMessage(messageText, "user");
            chatMessages.add(userMessage);
            chatAdapter.notifyItemInserted(chatMessages.size() - 1);
            scrollToBottom();
            
            // Xóa input
            messageEditText.setText("");
            
            // Hiển thị tin nhắn "đang nhập..."
            ChatMessage typingMessage = new ChatMessage("Đang nhập...", "bot");
            chatMessages.add(typingMessage);
            chatAdapter.notifyItemInserted(chatMessages.size() - 1);
            scrollToBottom();
            
            // Tạo phản hồi từ chatbot với lịch sử chat
            // Lấy lịch sử chat hiện tại (không bao gồm tin nhắn "đang nhập...")
            List<ChatMessage> chatHistory = new ArrayList<>(chatMessages);
            chatHistory.remove(chatHistory.size() - 1); // Xóa tin nhắn "đang nhập..."
            
            // Thêm log để debug
            Log.d("MainActivity", "Sending chat history with " + chatHistory.size() + " messages");
            for (int i = 0; i < chatHistory.size(); i++) {
                ChatMessage msg = chatHistory.get(i);
                Log.d("MainActivity", "Message " + i + ": " + msg.getSender() + " - " + msg.getMessage());
            }
            
            chatbotEngine.generateResponse(messageText, chatHistory, new ChatbotEngine.ResponseCallback() {
                @Override
                public void onResponseGenerated(String response) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Xóa tin nhắn "đang nhập..."
                            chatMessages.remove(chatMessages.size() - 1);
                            chatAdapter.notifyItemRemoved(chatMessages.size());
                            
                            // Lưu tin nhắn bot
                            chatbotDatabase.addMessage(response, "bot", currentSessionId);
                            ChatMessage botMessage = new ChatMessage(response, "bot");
                            chatMessages.add(botMessage);
                            chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                            scrollToBottom();
                        }
                    });
                }
            });
        }
    }

    private void scrollToBottom() {
        if (chatMessages.size() > 0) {
            chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
        }
    }

    private void clearChatHistory() {
        chatbotDatabase.clearSessionMessages(currentSessionId);
        chatMessages.clear();
        chatAdapter.notifyDataSetChanged();
        
        // Thêm tin nhắn chào mừng
        String welcomeMessage = "Xin chào! Tôi là Omen, trợ lý ảo của bạn. Tôi có thể giúp gì cho bạn?";
        chatbotDatabase.addMessage(welcomeMessage, "bot", currentSessionId);
        chatMessages.add(new ChatMessage(welcomeMessage, "bot"));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // Lưu tiêu đề cho session hiện tại trước khi thoát
        if (chatbotDatabase != null && !chatMessages.isEmpty()) {
            String title = "";
            for (ChatMessage message : chatMessages) {
                if ("user".equals(message.getSender())) {
                    title = message.getMessage();
                    break;
                }
            }
            
            if (title.isEmpty()) {
                title = "Chat mới";
            } else if (title.length() > 20) {
                // Cắt tiêu đề nếu quá dài và thêm dấu "..."
                title = title.substring(0, 20) + "...";
            }
            
            chatbotDatabase.updateSessionTitle(currentSessionId, title);
            chatbotDatabase.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_clear_history) {
            clearChatHistory();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
