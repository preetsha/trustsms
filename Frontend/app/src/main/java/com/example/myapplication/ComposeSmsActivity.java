package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.databinding.ActivityComposeSmsBinding;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashSet;
import java.util.Set;

public class ComposeSmsActivity extends AppCompatActivity {
    private ActivityComposeSmsBinding binding;
    private EditText textInput;
    private String phoneNumber;
    private final int PHONE_NUMBER_LENGTH = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityComposeSmsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar_compose_sms);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New Message");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    protected void onResume() {
        super.onResume();

        textInput = binding.editGchatMessage;

        binding.editRecipient.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                phoneNumber = s.toString().trim();
                setPhoneNumberInputStatus(binding.editRecipient, phoneNumber);
            }
        });

        binding.buttonGchatSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String msgBody = textInput.getText().toString();
                if (SMSContacts.getContactIndexByNumber(phoneNumber) == -1) {
                    Toast.makeText(getApplicationContext(), "Contact already exists.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!msgBody.isEmpty()
                        && phoneNumber.length() == PHONE_NUMBER_LENGTH
                        && isValidPhoneNumber(phoneNumber)) {
                    sendMessage();
                    String id = SMSContacts.getThreadIdbyPhoneNumber(getApplicationContext(), phoneNumber);
                    ContactDataModel c = new ContactDataModel(phoneNumber, id, msgBody, System.currentTimeMillis());
                    c.setPriority(ContactDataModel.Level.PRIORITY);
                    SMSContacts.contactList.add(c);

                    SharedPreferences preferences = getApplicationContext().getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);

                    final String cacheTrustedKey = "trustedList";
                    Set<String> list = preferences.getStringSet(cacheTrustedKey, new HashSet<>());
                    list.add(phoneNumber);
                    preferences.edit().remove(cacheTrustedKey).apply();
                    preferences.edit().putStringSet(cacheTrustedKey, list).apply();

                    onBackPressed();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        textInput = null;
        binding = null;
        phoneNumber = "";
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void sendMessage() {
        String msgBody = textInput.getText().toString();

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, msgBody, null, null);
        Toast.makeText(getApplicationContext(), "SMS sent.",
                Toast.LENGTH_LONG).show();

        textInput.setText("");
    }

    private void setPhoneNumberInputStatus(TextInputLayout til, String s) {
        if (s.length() < PHONE_NUMBER_LENGTH) {
            til.setError("Phone number too short");
        } else if (s.length() > PHONE_NUMBER_LENGTH) {
            til.setError("Phone number too long");
        } else if (!isValidPhoneNumber(s)) {
            til.setError("Please only enter numerical digits");
        } else {
            til.setError(null);
        }
    }

    private boolean isValidPhoneNumber(String s) {
        return !s.matches(".*\\D");
    }
}
