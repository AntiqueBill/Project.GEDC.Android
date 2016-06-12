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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.edu.hit.project.ec.loaders.user.LoginLoader;
import cn.edu.hit.project.ec.models.user.User;

public class LoginActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<User> {
    private ViewGroup rootView;
    @BindView(R.id.name) public TextInputLayout name;
    @BindView(R.id.password) public TextInputLayout password;
    @BindView(R.id.login) public Button login;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        rootView = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("EXPIRED", false)) {
            Snackbar.make(rootView, getString(R.string.error_auth_expired), Snackbar.LENGTH_LONG).show();
        }

        ButterKnife.bind(this);

        User user = User.load(preferences);
        if (user != null) {
            ((App) getApplication()).setUser(user);
            startActivity(new Intent(this, OverviewActivity.class));
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_register) {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<User> onCreateLoader(int id, Bundle args) {
        return new LoginLoader(LoginActivity.this, args.getString("name"), args.getString("password")) {
            @Override
            protected void onStartLoading() {
                login.setEnabled(false);
                login.setText(getString(R.string.common_logining));
                super.onStartLoading();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<User> loader, User user) {
        login.setEnabled(true);
        login.setText(getString(R.string.common_login));
        if (user != null) {
            user.save(preferences);
            ((App) getApplication()).setUser(user);
            startActivity(new Intent(this, OverviewActivity.class));
            finish();
        } else {
            Snackbar.make(rootView, getString(R.string.error_auth_wrong_username_or_password), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<User> loader) {

    }

    @OnClick(R.id.login)
    public void onLoginClick(View view) {
        String name = this.name.getEditText().getText().toString();
        String password = this.password.getEditText().getText().toString();
        if (name.equals("") || password.equals("")) {
            Snackbar.make(rootView, getString(R.string.error_auth_empty_username_or_password), Snackbar.LENGTH_LONG).show();
            return;
        }
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("password", password);
        getSupportLoaderManager().restartLoader(0, args, this);
    }
}
