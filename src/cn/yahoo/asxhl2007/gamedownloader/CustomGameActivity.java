package cn.yahoo.asxhl2007.gamedownloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.RadioGroup;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView.SavedState;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.SlidingDrawer;

public class CustomGameActivity extends Activity implements OnItemClickListener, OnItemLongClickListener, OnDrawerOpenListener, OnDrawerCloseListener{

    public static final String fileRoot = Environment.getExternalStorageDirectory().getAbsolutePath() + "/htmlGameDownload/games/";

    private final String key1 = "name";
    private final String key2 = "game";
    
    private Map<String, Game> gameMap = new HashMap<String, Game>();
    
    private ListView listView;
    
    private List<Map<String, String>> data;
    private SimpleAdapter adapter;
    
    private EditText etName;
    private EditText etWebSite;
    
    private RadioGroup rgScreenOrientation;
    
    private SlidingDrawer sd;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom);
        
        read();
        
        sd = (SlidingDrawer) findViewById(R.id.slidingDrawer);
        sd.setOnDrawerOpenListener(this);
        sd.setOnDrawerCloseListener(this);
        
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        etName = (EditText) findViewById(R.id.editTextName);
        etWebSite = (EditText) findViewById(R.id.editTextWebSite);
        rgScreenOrientation = (RadioGroup) findViewById(R.id.rgScreenOrientation);
        
        data = new ArrayList<Map<String, String>>();
        for(Map.Entry<String, Game> entry : gameMap.entrySet()){
            Game game = entry.getValue();
            Map<String, String> item1 = new HashMap<String, String>();
            item1.put(key1, game.name);
            item1.put(key2, "在线版 : " + game.urlRoot + game.urlResource);
            data.add(item1);
            Map<String, String> item2 = new HashMap<String, String>();
            item2.put(key1, game.name);
            item2.put(key2, "离线版 : " + game.localRoot + game.urlResource);
            data.add(item2);
        }
        
        adapter = new SimpleAdapter(this, data,
                R.layout.item,
                new String[] { key1, key2 }, new int[] { R.id.text1,
                        R.id.text2 });
        listView.setAdapter(adapter);
        
    }

    public void add(View view){
        final String name = etName.getText().toString();
        if(name.equals("")){
            showMessageDialog("请输入游戏名");
            return;
        }
        String webSite = etWebSite.getText().toString();
        if(!webSite.startsWith("http")){
            webSite = "http://" + webSite;
            etWebSite.setText(webSite);
        }
        String[] s = webSite.split("/");
        String urlRoot = "";
        String urlResource = "";
        if(webSite.length() == 0 || s.length < 3){
            showMessageDialog("请输入合法的网址");
            return;
        }
        
        for(int i = 0; i < s.length; i++){
            if(i < 3){
                urlRoot += s[i] + "/";
            }else{
                urlResource += i == s.length - 1 ? s[i] : s[i] + "/" ;
            }
        }
        
        int screenOrientation = rgScreenOrientation.indexOfChild(rgScreenOrientation.findViewById(rgScreenOrientation.getCheckedRadioButtonId()));
        
        final Game game = new Game(name, urlRoot, urlResource, screenOrientation);

        Map<String, String> item1 = new HashMap<String, String>();
        item1.put(key1, game.name);
        item1.put(key2, "在线版 : " + game.urlRoot + game.urlResource);
        data.add(item1);
        Map<String, String> item2 = new HashMap<String, String>();
        item2.put(key1, game.name);
        item2.put(key2, "离线版 : " + game.localRoot + game.urlResource);
        data.add(item2);
        adapter.notifyDataSetChanged();

        gameMap.put(name, game);
        
        new Thread(){public void run(){
            write();
        }}.start();
        
        sd.close();
        showKeyboard(this, false);
    }
    
    public void export(View v){
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            showMessageDialog("SD未加载！");
            return;
        }
        File file = new File(fileRoot + "gameList.txt");
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        if(file.exists()){
            try {
                file.delete();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        PrintStream ps = null;
        try {
            StringBuilder sb = new StringBuilder();
            for(Game game : gameMap.values()){
                sb.append(game.name);
                sb.append("\n");
                sb.append(game.urlRoot + game.urlResource);
                sb.append("\n\n");
            }

            ps = new PrintStream(file);
            ps.print(sb.toString());
            showMessageDialog("导出成功！\n地址：" + file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            showMessageDialog("对不起，导出失败！");
        }finally{
            if(ps != null){
                ps.flush();
                ps.close();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String urlRoot = "";
        Map<String, String> item = (Map<String, String>)data.get(position);
        Game game = gameMap.get(item.get(key1));
        if(position % 2 == 1){
            File index = new File(game.localRoot + game.urlResource);
            if(!index.exists()){
                showMessageDialog("该游戏尚未下载过，请先运行在线版，程序将会自动下载该游戏！");
                return;
            }
            urlRoot = "file:///" + game.localRoot;
        }else{
            urlRoot = game.urlRoot;
        }
        
        Intent intent = new Intent(this, GameDownloaderActivity.class);
        intent.putExtra("urlRoot", urlRoot);
        intent.putExtra("urlResource", game.urlResource);
        intent.putExtra("localRoot",  game.localRoot);
        intent.putExtra("landspace", game.screenOrientation);
        startActivity(intent);
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        showDeleteDialog(position);
        return true;
    }

    private void showMessageDialog(String errMsg){
        AlertDialog ad = new AlertDialog.Builder(this).create();
        ad.setMessage(errMsg);
        ad.setButton("确认", new OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ad.show();
    }
    
    private void showDeleteDialog(final int position){
        AlertDialog ad = new AlertDialog.Builder(this).create();
        ad.setMessage("确认要删除？");
        ad.setButton("确认", new OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gameMap.remove(data.get(position).get(key1));
                new Thread(){public void run(){
                    write();
                }}.start();
                
                int index = (position / 2) * 2;
                data.remove(index + 1);
                data.remove(index);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        ad.setButton2("取消", new OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ad.show();
    }

    private static class Game implements Serializable{
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        private String name;
        private String urlRoot;
        private String urlResource;
        private String localRoot;
        private int screenOrientation;
        
        Game(String name, String urlRoot, String urlResource, int screenOrientation){
            this.name = name;
            this.urlRoot = urlRoot;
            this.urlResource = urlResource;
            this.screenOrientation = screenOrientation;
            localRoot = fileRoot + MD5Util.getMD5(urlRoot + urlResource) + "/";
        }
    }
    
    @SuppressWarnings("unchecked")
    private void read(){
        File file = new File(getFilesDir().getAbsolutePath() + "/gameMap");
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            gameMap = (Map<String, Game>) ois.readObject();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private void write(){
        File file = new File(getFilesDir().getAbsolutePath() + "/gameMap");
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(gameMap);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }
    
    public static void showKeyboard(Activity activity, boolean showIt) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view != null) {
            if (showIt) {
                imm.showSoftInput(view, 0); // 显示软键盘
            } else {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);// 隐藏软键盘
            }
        }
    }

    @Override
    public void onDrawerClosed() {
        showKeyboard(this, false);
    }

    @Override
    public void onDrawerOpened() {
        
    }
}
