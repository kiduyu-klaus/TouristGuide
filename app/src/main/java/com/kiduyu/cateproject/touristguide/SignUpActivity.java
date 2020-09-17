package com.kiduyu.cateproject.touristguide;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class SignUpActivity extends AppCompatActivity {

    EditText edtFullName;


    EditText edtEmail;

    EditText edtPassword;

    EditText edtMobile;

    Button btnSignUp;

    String strFullname, strEmail, strPassword, strMobi, strMessage;


    TextView txtLogin;
    JsonUtils jsonUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        JsonUtils.setStatusBarGradiant(SignUpActivity.this);
        jsonUtils = new JsonUtils(this);
        jsonUtils.forceRTLIfSupported(getWindow());

        edtFullName = findViewById(R.id.editText_name_register);
        edtEmail = findViewById(R.id.editText_email_register);
        edtPassword = findViewById(R.id.editText_password_register);
        edtMobile = findViewById(R.id.editText_phoneNo_register);

        btnSignUp = findViewById(R.id.button_submit);
        txtLogin = findViewById(R.id.textView_login_register);

        btnSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                strFullname = edtFullName.getText().toString().replace(" ", "%20");
                strEmail = edtEmail.getText().toString();
                strPassword = edtPassword.getText().toString();
                strMobi = edtMobile.getText().toString();

                if (TextUtils.isEmpty(strFullname)) {
                   edtFullName.setError("Required");
                } else if (TextUtils.isEmpty(strEmail)) {
                    edtEmail.setError("Required");
                } else if (TextUtils.isEmpty(strPassword)) {
                    edtPassword.setError("Required");
                } else if (TextUtils.isEmpty(strMobi)) {
                    edtMobile.setError("Required");
                } else {
                    JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
                    jsObj.addProperty("method_name", "user_register");
                    jsObj.addProperty("name", strFullname);
                    jsObj.addProperty("email", strEmail);
                    jsObj.addProperty("password", strPassword);
                    jsObj.addProperty("phone", strMobi);

                    if (JsonUtils.isNetworkAvailable(SignUpActivity.this)) {
                        new MyTaskRegister(API.toBase64(jsObj.toString())).execute(Constant.API_URL);
                    } else {
                        showToast(getString(R.string.network_msg));
                    }
                }

            }
        });

        txtLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class MyTaskRegister extends AsyncTask<String, Void, String> {

        String base64;

        private MyTaskRegister(String base64) {
            this.base64 = base64;
        }

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SignUpActivity.this);
            pDialog.setMessage(getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0], base64);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (null != pDialog && pDialog.isShowing()) {
                pDialog.dismiss();
            }
            if (null == result || result.length() == 0) {
                showToast(getString(R.string.no_data_found));
            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.CATEGORY_ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        if(objJson.has("status")){
                            showToast(getString(R.string.no_data_found));
                        }else {
                            strMessage = objJson.getString(Constant.MSG);
                            Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);

                            Log.d("TAG", "successmessage: "+Constant.GET_SUCCESS_MSG);
                        }}

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult();
            }

        }
    }

    public void setResult() {

        if (Constant.GET_SUCCESS_MSG == 0) {
            edtEmail.setText("");
            edtEmail.requestFocus();
            final PrettyDialog dialog = new PrettyDialog(this);
            dialog.setTitle(getString(R.string.dialog_error))
                    .setTitleColor(R.color.dialog_text)
                    .setMessage(strMessage)
                    .setMessageColor(R.color.dialog_text)
                    .setAnimationEnabled(false)
                    .setIcon(R.drawable.pdlg_icon_close, R.color.dialog_color, new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            dialog.dismiss();
                        }
                    })
                    .addButton(getString(R.string.dialog_ok), R.color.dialog_white_text, R.color.dialog_color, new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            dialog.dismiss();
                        }
                    });
            dialog.setCancelable(false);
            dialog.show();
        } else {
            final PrettyDialog dialog = new PrettyDialog(this);
            dialog.setTitle(getString(R.string.dialog_success))
                    .setTitleColor(R.color.dialog_text)
                    .setMessage(strMessage)
                    .setMessageColor(R.color.dialog_text)
                    .setAnimationEnabled(false)
                    .setIcon(R.drawable.pdlg_icon_success, R.color.dialog_color, new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            dialog.dismiss();
                        }
                    })
                    .addButton(getString(R.string.dialog_ok), R.color.dialog_white_text, R.color.dialog_color, new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            dialog.dismiss();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    });
            dialog.setCancelable(false);
            dialog.show();

        }
    }

    public void showToast(String msg) {
        Toast.makeText(SignUpActivity.this, msg, Toast.LENGTH_LONG).show();
    }
}