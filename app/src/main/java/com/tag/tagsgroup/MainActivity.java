package com.tag.tagsgroup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity {

    private final static TestModel[] initAdd = new TestModel[]{new TestModel(0, "标签1"), new TestModel(1, "标签2")};
    private final static TestModel[] initHot = new TestModel[]{new TestModel(0, "标签1"), new TestModel(1, "标签2"), new TestModel(2, "测试"),
            new TestModel(3, "微信"), new TestModel(4, "qq"), new TestModel(5, "ggg")};
    private final static TestModel[] initAll = new TestModel[]{new TestModel(0, "标签1"), new TestModel(1, "标签2"), new TestModel(2, "测试"),
            new TestModel(3, "微信"), new TestModel(4, "qq"), new TestModel(5, "淘宝"), new TestModel(6, "UC"), new TestModel(7, "10086"),
            new TestModel(8, "支付宝"), new TestModel(9, "tag"), new TestModel(10, "热门"), new TestModel(11, "lbe"), new TestModel(12, "ggg")};

    private TagsGroup add;
    private TagsGroup hot;
    private TagsGroup search;

    private String searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        add = (TagsGroup) findViewById(R.id.add);
        hot = (TagsGroup) findViewById(R.id.hot);
        search = (TagsGroup) findViewById(R.id.search);

        add.setTags(initAdd);
        hot.setTags(initHot);
        hot.setSelectedTag(true, initAdd);

        add.setOnTagTextChangedListener(s -> {
            if (TextUtils.isEmpty(s)) {
                hot.setVisibility(View.VISIBLE);
                search.setVisibility(View.GONE);
                search.clearTag();
            } else {
                if (!s.equals(searchText)) {
                    hot.setVisibility(View.GONE);
                    search.setVisibility(View.VISIBLE);
                    Observable.timer(600, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                            .flatMap(aLong -> Observable.from(initAll))
                            .filter(testModel -> testModel.getTag().contains(searchText))
                            .subscribe(testModel1 -> search.appendTag(testModel1), Throwable::printStackTrace);
                }
            }
            searchText = s;
        });
        add.setOnTagChangeListener(new TagsGroup.OnTagChangeListener() {
            @Override
            public void onAppend(TagsGroup tagGroup, String tag) {
                showHot(tag, true);
            }

            @Override
            public void onDelete(TagsGroup tagGroup, Object objTag, String tag) {
                showHot(tag, false);
            }
        });
        hot.setOnTagClickListener((viewGroup, v, objTag, tag, isSelected) -> {
            boolean canAdd = canAdd();
            if (!isSelected) {
                if (canAdd) {
                    v.setSelected(true);
                    add.addTag(tag, objTag, false);
                } else {
                    Toast.makeText(this, R.string.add_tag_limit_hint, Toast.LENGTH_SHORT).show();
                }
            } else {
                add.removeTag(tag);
            }
        });
        search.setOnTagClickListener((viewGroup, v, objTag, tag, isSelected) -> {
            if (canAdd()) {
                if (add.addTag(tag, true)) {

                }
                search.clearTag();
                showHot(tag, true);
            } else {
                Toast.makeText(this, R.string.add_tag_limit_hint, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean canAdd() {
        return add.canAdd();
    }

    private void showHot(String tag, boolean selected) {
        searchText = "";
        hot.setVisibility(View.VISIBLE);
        search.setVisibility(View.GONE);
        hot.setSelectedTag(tag, selected);
    }

    private void search(String searchText) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
