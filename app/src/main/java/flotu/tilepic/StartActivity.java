package flotu.tilepic;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ListView list = (ListView) findViewById(R.id.listview);

        List<String> urls = new ArrayList<>();
        urls.add("http://s7.pikabu.ru/post_img/2014/08/17/8/1408276919_215680681.jpg");
        urls.add("http://s5.pikabu.ru/post_img/2014/08/17/6/1408262652_664424978.jpg");
        urls.add("http://cs540102.vk.me/c540106/v540106140/19ce4/lOJHVJ4lfdc.jpg");
        urls.add("http://cs540102.vk.me/c540106/v540106349/13c53/hL8c9lUbUA0.jpg");
        urls.add("http://cs540102.vk.me/c540104/v540104697/380f1/7alL7zdXxf4.jpg");
        urls.add("http://cs540102.vk.me/c540104/v540104697/380de/RPxL_wGDShY.jpg");

        Tilepic.with(this).put(urls).into(list);
    }
}
