package com.kiduyu.cateproject.touristguide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Password;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.refactor.library.SmoothCheckBox;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity {
    String strEmail, strPassword, strMessage, strName, strPassengerId;
    EditText edtEmail;
    EditText edtPassword;
    private Validator validator;
    Button btnSingIn, btnSkip;
    MyApplication MyApp;
    TextView textForgot, textSignUp;
    JsonUtils jsonUtils;
    public static final String mypreference = "mypref";
    public static final String pref_email = "pref_email";
    public static final String pref_password = "pref_password";
    public static final String pref_check = "pref_check";
    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;
    boolean iswhichscreen;
    String detail_screen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        JsonUtils.setStatusBarGradiant(this);


        jsonUtils = new JsonUtils(this);
        jsonUtils.forceRTLIfSupported(getWindow());
        pref = getSharedPreferences(mypreference, 0); // 0 - for private mode
        editor = pref.edit();


        MyApp = MyApplication.getAppInstance();
        edtEmail = findViewById(R.id.editText_email_login_activity);
        edtPassword = findViewById(R.id.editText_password_login_activity);
        btnSingIn = findViewById(R.id.button_login_activity);
        btnSkip = findViewById(R.id.button_skip_login_activity);

        textForgot = findViewById(R.id.textView_forget_password_login);
        textSignUp = findViewById(R.id.textView_signup_login);
        final SmoothCheckBox checkBox = findViewById(R.id.checkbox_login_activity);
        checkBox.setChecked(false);
        if (pref.getBoolean(pref_check, false)) {
            edtEmail.setText(pref.getString(pref_email, null));
            edtPassword.setText(pref.getString(pref_password, null));
            checkBox.setChecked(true);
        } else {
            edtEmail.setText("");
            edtPassword.setText("");
            checkBox.setChecked(false);
        }

        checkBox.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                Log.d("SmoothCheckBox", String.valueOf(isChecked));
                if (isChecked) {
                    editor.putString(pref_email, edtEmail.getText().toString());
                    editor.putString(pref_password, edtPassword.getText().toString());
                    editor.putBoolean(pref_check, true);
                    editor.commit();
                } else {
                    editor.putBoolean(pref_check, false);
                    editor.commit();
                }
            }
        });
        btnSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strEmail = edtEmail.getText().toString();
                strPassword = edtPassword.getText().toString();

                if (TextUtils.isEmpty(strEmail)){
                    edtEmail.setError("Required");

                } else  if (TextUtils.isEmpty(strPassword)){
                    edtPassword.setError("Required");

                } else {
                    JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
                    jsObj.addProperty("method_name", "user_login");
                    jsObj.addProperty("email", strEmail);
                    jsObj.addProperty("password", strPassword);
                    if (JsonUtils.isNetworkAvailable(LoginActivity.this)) {
                        new MyTaskLogin(API.toBase64(jsObj.toString())).execute(Constant.API_URL);
                    }
                }
            }
        });
        textSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        textForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                */
            }
        });


    }



    @SuppressLint("StaticFieldLeak")
    private class MyTaskLogin extends AsyncTask<String, Void, String> {

        String base64;

        private MyTaskLogin(String base64) {
            this.base64 = base64;
        }

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
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
                            if (objJson.has(Constant.MSG)) {
                                strMessage = objJson.getString(Constant.MSG);
                                Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                            } else {
                                Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                                strName = objJson.getString(Constant.USER_NAME);
                                strPassengerId = objJson.getString(Constant.USER_ID);
                            }}
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult();
            }

        }
    }


    public void setResult() {

        if (Constant.GET_SUCCESS_MSG == 0) {
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
            MyApp.saveIsLogin(true);
            MyApp.saveLogin(strPassengerId, strName, strEmail);
            if(iswhichscreen)
            {
                Intent i = new Intent(LoginActivity.this, ActivityDetail.class);
                i.putExtra("isfromdetail",detail_screen);
                i.putExtra("Id",Constant.LATEST_PLACE_IDD);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
            else {
                ActivityCompat.finishAffinity(LoginActivity.this);
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }

        }
    }

    public void showToast(String msg) {
        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
    }
}