package funix.prm.project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import funix.prm.project.R;
import funix.prm.project.model.User;
import funix.prm.project.sqlite.SQLiteHelper;

public class SignUpActivity extends AppCompatActivity {

    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {

                case R.id.sign_up_btn:

                    //TODO: Logic when press Signup
                    EditText edtUserName = findViewById(R.id.edt_username);
                    EditText edtPassword = findViewById(R.id.edt_password);
                    EditText edtConfirmPassword = findViewById(R.id.confirm_password);
                    Boolean check = SQLiteHelper.getInstance(getBaseContext()).isCheckUserExist(edtUserName.getText().toString());

                    //information must be full
                    if (edtUserName.getText().toString().equals("") || edtPassword.getText().toString().equals("")||edtConfirmPassword.getText().toString().equals("")){
                        Toast.makeText(SignUpActivity.this, "“Please check your information!”", Toast.LENGTH_SHORT).show();

                    //already registered
                    } else if (check){
                        Toast.makeText(SignUpActivity.this, "Already registered", Toast.LENGTH_SHORT).show();

                        //pass is not matched
                    } else if (!edtPassword.getText().toString().equals(edtConfirmPassword.getText().toString())) {
                        Toast.makeText(SignUpActivity.this, "The passwords you entered do not match", Toast.LENGTH_LONG).show();

                     //add user, no info about played videos
                    } else if (!check && !edtUserName.getText().toString().equals("") && !edtPassword.getText().toString().equals("")&& !edtConfirmPassword.getText().toString().equals("")
                            && edtPassword.getText().toString().equals(edtConfirmPassword.getText().toString())){
                        User user = new User(edtUserName.getText().toString(),edtPassword.getText().toString(),"","","","");
                        SQLiteHelper.getInstance(getBaseContext()).addUser(user);
                        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                        startActivity(intent);
                        //turnscreen with animattion
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                    break;

                case R.id.sign_in_tv:

                    //TODO: Logic when press "Sign in, please click here!"
                    Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                    startActivity(intent);
                    //turnscreen with animattion
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //TODO: Init and set views
        final Button btnSignUp = findViewById(R.id.sign_up_btn);
        btnSignUp.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onClick(btnSignUp);
                    }
                }
        );

        final TextView tvSignIn = findViewById(R.id.sign_in_tv);
        tvSignIn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onClick(tvSignIn);
                    }
                }
        );
    }
}