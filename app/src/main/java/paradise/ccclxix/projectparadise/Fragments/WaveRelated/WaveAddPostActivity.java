package paradise.ccclxix.projectparadise.Fragments.WaveRelated;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import paradise.ccclxix.projectparadise.R;

public class WaveAddPostActivity extends AppCompatActivity {

    private TextView waveAddPostUsername;
    private TextView waveAddPostWave;
    private EditText waveAddPostMessage;
    private ImageView waveAddPostThumbnail;
    private ImageView waveAddPostInsertImage;
    private ImageView waveAddPostImage;
    private ImageView waveAddPostCreatePost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wave_add_post);

        waveAddPostUsername = findViewById(R.id.wave_add_post_username);
        waveAddPostWave = findViewById(R.id.wave_add_post_wave);
        waveAddPostMessage = findViewById(R.id.wave_add_post_message);
        waveAddPostThumbnail = findViewById(R.id.wave_add_post_thumbnail);
        waveAddPostInsertImage = findViewById(R.id.wave_add_post_insert_image);
        waveAddPostImage = findViewById(R.id.wave_add_post_image);
        waveAddPostCreatePost = findViewById(R.id.wave_add_post_send);
    }
}
