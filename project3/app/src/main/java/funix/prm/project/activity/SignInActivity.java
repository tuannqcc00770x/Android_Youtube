package funix.prm.project.activity;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import org.json.JSONObject;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import funix.prm.project.R;
import funix.prm.project.model.User;
import funix.prm.project.sqlite.SQLiteHelper;
import static funix.prm.project.util.Constants.DB_NAME;
import static funix.prm.project.util.Constants.ID;
import static funix.prm.project.util.Constants.USER;

public class SignInActivity extends AppCompatActivity {

    private static SignInActivity mainActivity;
    private CallbackManager callbackManager;
    private FacebookCallback<LoginResult> loginResult;
    String faceName;
    String faceID;

    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();

            switch (id) {

                case R.id.sign_in_btn:
                    //TODO: Logic when press Signin
                    EditText edt_UserName = findViewById(R.id.edt_username);
                    EditText edt_Password = findViewById(R.id.edt_password);
                    Boolean check = SQLiteHelper.getInstance(getBaseContext()).isCheckUserExist(edt_UserName.getText().toString(),edt_Password.getText().toString());
                    if (check){
                        Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                        intent.putExtra(USER,edt_UserName.getText().toString());//send user name
                        intent.putExtra(ID,"");//if "EMTY", user login with normal account. if "NOT EMTY", user login with Facebook account
                        startActivity(intent);
                        //Turn screen by animation
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    } else {
                        Toast.makeText(SignInActivity.this, "Sorry, your username and password are incorrect. Please try again!", Toast.LENGTH_LONG).show();
                    }
                    break;

                case R.id.sign_up_tv:
                    //TODO: logic when click "Sign up, please click here!"
                    Intent intent = new Intent(SignInActivity.this,SignUpActivity.class);
                    startActivity(intent);
                    //Turn screen by animation
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    break;

                case R.id.loginFb:
                    //TODO: logic when login with Faceboook
                    loginFaceBook();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        //this.deleteDatabase(DB_NAME);

        //TODO: Init and set views
        final TextView tvSignUp = findViewById(R.id.sign_up_tv);
        tvSignUp.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onClick(tvSignUp);
                    }
                }
        );

        final Button btnSignIn = findViewById(R.id.sign_in_btn);
        btnSignIn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onClick(btnSignIn);
                    }
                }
        );
        final Button loginButton = (Button) findViewById(R.id.loginFb);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onClick(loginButton);
            }
        });
        printKeyHash(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    //Login facebook with permisstion
    public void loginFaceBook() {
        if(AccessToken.getCurrentAccessToken() != null)
        {
            LoginManager.getInstance().logOut();
        }
        initFaceBook();
        LoginManager.getInstance().registerCallback(callbackManager, loginResult);
        LoginManager.getInstance().logInWithReadPermissions(mainActivity, Arrays.asList("public_profile", "user_friends","email"));
    }
    //Check login facebook
    public boolean isLoggedInFaceBook() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    //Get Avatar
    public URL extractFacebookIcon(String id) {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            URL imageURL = new URL("http://graph.facebook.com/" + id
                    + "/picture?type=large");
            return imageURL;
        } catch (Throwable e) {
            return null;
        }
    }

    public void initFaceBook () {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        mainActivity = this;
        loginResult = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //Logic when login facebook successfully
                GraphRequest request = GraphRequest.newMeRequest(
                        AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object,
                                                    GraphResponse response) {
                                // Application code
                                String name = object.optString(getString(R.string.name));
                                faceName = name;
                                String id = object.optString(getString(R.string.id));
                                faceID = id;
                                String email = object.optString(getString(R.string.email));
                                String link = object.optString(getString(R.string.link));
                                URL imageURL = extractFacebookIcon(id);
                                Log.d("name: ",name);
                                Log.d("id: ",id);
                                Log.d("email: ",email);
                                Log.d("link: ",link);
                                Log.d("imageURL: ",imageURL.toString());
                                User user = new User(faceID,"","","","","");
                                SQLiteHelper.getInstance(getBaseContext()).addUser(user);
                                Intent intentFacebook = new Intent(SignInActivity.this,HomeActivity.class);
                                intentFacebook.putExtra(USER,faceName);//send user name
                                intentFacebook.putExtra(ID,faceID);//if "EMTY", user login with normal account. if "NOT EMTY", user login with Facebook account
                                startActivity(intentFacebook);

                                //Turn screen with animation
                                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString(getString(R.string.fields), getString(R.string.fields_name));
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        };
    }

    //Get keyHash
    public String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {

            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (android.content.pm.Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }
}
