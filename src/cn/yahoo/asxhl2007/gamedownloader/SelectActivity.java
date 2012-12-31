package cn.yahoo.asxhl2007.gamedownloader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SelectActivity extends ListActivity {

    public static final String fileRoot = Environment.getExternalStorageDirectory().getAbsolutePath() + "/htmlGameDownload/games/";

    private final String key1 = "name";
    private final String key2 = "game";
    
    private Map<String, Game> gameMap = new HashMap<String, Game>();
    
    private void addAll(){
        Game game1 = new Game("迷失岛", "http://game.duopao.com/show/project/", "LOST/index.html");
        gameMap.put("迷失岛", game1);

        Game game2 = new Game("小炮塔", "http://splax.net/", "m/game10.html");
        gameMap.put("小炮塔", game2);

        Game game3 = new Game("斗地主", "http://game.duopao.com/duopao/", "doudizhu/doudizhu.htm");
        gameMap.put("斗地主", game3);

        Game game4 = new Game("立方体", "http://game.duopao.com/duopao/", "Cubium/Cubium.htm");
        gameMap.put("立方体", game4);

        Game game5 = new Game("装甲奇兵", "http://www.snappygames.com/", "game/nano-tanks.aspx");
        gameMap.put("装甲奇兵", game5);

        Game game6 = new Game("绒球大战", "http://www.dseffects.com/games/", "Fillit/Fillit.php");
        gameMap.put("绒球大战", game6);

        Game game7 = new Game("灭尸行动", "http://game.duopao.com/show/project/", "liansuobaozha/liansuobaozha.html");
        gameMap.put("灭尸行动", game7);

        Game game8 = new Game("大炮塔防", "http://www.snappygames.com/game/big-guns-tower-defense.aspx", "big-guns-tower-defense.aspx");
        gameMap.put("大炮塔防", game8);

        Game game9 = new Game("逃脱", "http://splax.net/m/", "agame2.html");
        gameMap.put("逃脱", game9);

        Game game10 = new Game("空间先生", "http://game.duopao.com/duopao/", "spaceman/spaceman.html");
        gameMap.put("空间先生", game10);

        Game game11 = new Game("铁路大亨", "http://game.duopao.com/show/project/", "RailMaze/RailMaze.html");
        gameMap.put("铁路大亨", game11);

        Game game12 = new Game("巴别塔", "http://www.dseffects.com/games/", "Babel/Babel.php");
        gameMap.put("巴别塔", game12);

        Game game13 = new Game("木乃伊归来", "http://game.duopao.com/duopao/", "Mummy/munaiyiguilai.html");
        gameMap.put("木乃伊归来", game13);

        Game game14 = new Game("飞屋环游记", "http://game.duopao.com/show/project/", "feiwuhuanyouji/feiwuhuanyouji.html");
        gameMap.put("飞屋环游记", game14);

        Game game15 = new Game("降魔塔防", "http://www.snappygames.com/game/", "red-wizard-tower-defense.aspx");
        gameMap.put("降魔塔防", game15);
        
        Game game16 = new Game("坦克城战", "http://www.snappygames.com/game/", "mini-tank-battle.aspx");
        gameMap.put("坦克城战", game16);
        
        Game game17 = new Game("蜂窝扫雷", "http://www.desertdogdev.com/", "hexsweeper.html");
        gameMap.put("蜂窝扫雷", game17);

        Game game18 = new Game("小小塔防", "http://game.duopao.com/duopao/", "TinyDefense/TinyDefense.htm");
        gameMap.put("小小塔防", game18);

        Game game19 = new Game("造梦西游3（flash）", "http://www.4399.com/", "flash/78072.htm");
        gameMap.put("造梦西游3（flash）", game19);

        Game game20 = new Game("切水果（flash）", "http://www.4399.com/", "flash/73386.htm");
        gameMap.put("切水果（flash）", game20);

        Game game21 = new Game("猫里奥（flash）", "http://www.4399.com/", "flash/91723_2.htm");
        gameMap.put("猫里奥（flash）", game21);

        Game game22 = new Game("宝石翻转", "http://game.duopao.com/", "duopao/SeaTreasureMatch/SeaTreasureMatch.htm");
        gameMap.put("宝石翻转", game22);

        Game game23 = new Game("块灰机", "http://lufy.netne.net/", "lufylegend-js/lufylegend-1.4/shelter/");
        gameMap.put("块灰机", game23);
        
    }
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addAll();

        List<Map<String, ?>> items = new ArrayList<Map<String, ?>>();
        for(Map.Entry<String, Game> entry : gameMap.entrySet()){
            Game game = entry.getValue();
            Map<String, String> item1 = new HashMap<String, String>();
            item1.put(key1, game.name);
            item1.put(key2, "在线版 : " + game.urlRoot + game.urlResource);
            items.add(item1);
            Map<String, String> item2 = new HashMap<String, String>();
            item2.put(key1, game.name);
            item2.put(key2, "离线版 : " + game.localRoot + game.urlResource);
            items.add(item2);
        }
        
        SimpleAdapter adapter = new SimpleAdapter(this, items,
                android.R.layout.simple_list_item_2,
                new String[] { key1, key2 }, new int[] { android.R.id.text1,
                        android.R.id.text2 });
        setListAdapter(adapter);
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String urlRoot = "";
        @SuppressWarnings("unchecked")
        Map<String, String> item = (Map<String, String>)l.getItemAtPosition(position);
        Game game = gameMap.get(item.get(key1));
        if(position % 2 == 1){
            File index = new File(game.localRoot + game.urlResource);
            if(!index.exists()){
                showErrorDialog();
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
        startActivity(intent);
        
        super.onListItemClick(l, v, position, id);
    }

    private void showErrorDialog(){
        AlertDialog ad = new AlertDialog.Builder(this).create();
        ad.setMessage("该游戏尚未下载过，请先运行在线版，程序将会自动下载该游戏！");
        ad.setButton("确认", new OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ad.show();
    }

    private class Game{
        private String name;
        private String urlRoot;
        private String urlResource;
        private String localRoot;
        
        Game(String name, String urlRoot, String urlResource){
            this.name = name;
            this.urlRoot = urlRoot;
            this.urlResource = urlResource;
            localRoot = fileRoot + MD5Util.getMD5(urlRoot + urlResource) + "/";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }
}
