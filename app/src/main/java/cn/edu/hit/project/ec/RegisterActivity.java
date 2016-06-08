package cn.edu.hit.project.ec;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.edu.hit.project.ec.loaders.user.RegisterLoader;
import cn.edu.hit.project.ec.models.user.User;

public class RegisterActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<User> {
    private ViewGroup rootView;
    @BindView(R.id.name) public TextInputLayout name;
    @BindView(R.id.email) public TextInputLayout email;
    @BindView(R.id.password) public TextInputLayout password;
    @BindView(R.id.register) public Button register;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        rootView = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_login) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<User> onCreateLoader(int id, Bundle args) {
        return new RegisterLoader(this, args.getString("name"), args.getString("email"), args.getString("password")) {
            @Override
            protected void onStartLoading() {
                register.setEnabled(false);
                register.setText(getString(R.string.common_registering));
                super.onStartLoading();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<User> loader, User user) {
        register.setEnabled(true);
        register.setText(getString(R.string.common_register));
        if (user != null) {
            user.save(preferences);
            ((App) getApplication()).setUser(user);
            startActivity(new Intent(this, OverviewActivity.class));
            finish();
        } else {
            Snackbar.make(rootView, getString(R.string.error_auth_register_failed), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<User> loader) {

    }

    @OnClick(R.id.register)
    public void onRegisterClick(View view) {
        String name = this.name.getEditText().getText().toString();
        String email = this.email.getEditText().getText().toString();
        String password = this.password.getEditText().getText().toString();
        if (name.equals("") || email.equals("") || password.equals("")) {
            Snackbar.make(rootView, getString(R.string.error_auth_empty_username_or_email_or_password), Snackbar.LENGTH_LONG).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Snackbar.make(rootView, getString(R.string.error_auth_not_valid_email), Snackbar.LENGTH_LONG).show();
            return;
        }
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("email", email);
        args.putString("password", password);
        getSupportLoaderManager().restartLoader(0, args, this);
    }
}
