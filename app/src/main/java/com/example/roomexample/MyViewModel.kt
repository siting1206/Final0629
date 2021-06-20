package com.example.roomexample


import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.roomexample.database.Scene
import com.example.roomexample.database.SceneDatabase
import com.example.roomexample.database.SceneDatabaseDao
import kotlinx.coroutines.launch

//viewmodel need to know the database dao (passed argument here) for accessing data from the database
//class MyViewModel(dataSource: SceneDatabaseDao) : ViewModel(){

//use the AndroidViewModel with default parameter: application context
class MyViewModel(application: Application) : AndroidViewModel(application) {

    //get the reference to the database dao
    private val database = SceneDatabase.getInstance(application).sceneDatabaseDao

    //a list of all scenes or matched scenes from the database (LiveData)
    val sceneList = MediatorLiveData<List<Scene>>()

    //get the selected scene (livedata for easy observing)
    val selectedScene = MutableLiveData<Scene>()

    //list of cities in Taiwan
    //the following way is sometime not working: don't know
    val cityList : Array<String> = application.resources.getStringArray(R.array.township_array)
//    val cityList = arrayOf(
//        "花蓮縣", "台東縣", "宜蘭縣", "屏東縣", "台東縣", "高雄市",
//        "雲林縣", "彰化縣", "臺南市", "嘉義縣", "嘉義市", "臺中市", "臺北市",
//        "新北市", "新竹縣", "新竹市", "基隆市", "苗栗縣", "桃園市", "南投縣", "澎湖縣",
//        "金門縣"
//    )

    init {
        getAllScenes()
    }

    fun getAllScenes() {  //set the livedata source of the sceneList to be all scenes
        sceneList.addSource(database.loadAllScenes()) { scenes ->
            sceneList.setValue(scenes)
        }
    }

    fun searchAllScenes(name: String) { //set the livedata source of the sceneList to be matched scenes
        sceneList.addSource(database.findScenes(name)) { scenes ->
            sceneList.setValue(scenes)
        }
    }

    fun getScene(sceneId: Long) {  //get one scene from the sceneList
        sceneList.value?.let {
            selectedScene.value = it.find { it.id == sceneId }
        }
    }

    fun insertScene(newScene: Scene) {  //add a new scene into the database
        viewModelScope.launch {
            database.insertScene(newScene)
        }
    }

    fun updateScene(oldScene: Scene) {  //add a new scene into the database
        viewModelScope.launch {
            database.updateScene(oldScene)
        }
    }

    fun deleteScene(oldScene: Scene) {  //delete a scene from the database
        viewModelScope.launch {
            database.deleteScene(oldScene)
        }
    }

    /*fun initDB() {  //setup the initial database
        viewModelScope.launch {
            repeat(1) {
                database.insertScene(
                    Scene(
                        "花蓮縣",
                        "長春祠",
                        "花蓮縣秀林鄉長春祠",
                        R.drawable.photo1_0,
                        "為紀念開闢中橫公路殉職人員所建，祠旁湧泉長年流水成散瀑，公路局取名為「長春飛瀑」，成為中橫公路具特殊意義的地標。"
                    )
                )
                database.insertScene(
                    Scene(
                        "花蓮縣",
                        "燕子口",
                        "花蓮縣秀林鄉燕子口",
                        R.drawable.photo1_1,
                        "燕子口步道從燕子口到靳珩橋，途中可欣賞太魯閣峽谷、壺穴、湧泉、印地安酋長岩等景觀。"
                    )
                )
                database.insertScene(
                    Scene(
                        "花蓮縣",
                        "慈母橋",
                        "花蓮縣秀林鄉慈母橋",
                        R.drawable.photo1_2,
                        "慈母橋是一座形狀美麗的紅色大橋，位於天祥以東三公里處的中橫公路上，為立霧溪與其支流荖西溪的匯流處。"
                    )
                )
                database.insertScene(
                    Scene(
                        "新北市",
                        "天元宮",
                        "新北市淡水無極天元宮",
                        R.drawable.photo0_0,
                        "擁有五層圓型寶塔的壯觀寺廟，每逢櫻花季會吸引大批人潮。"
                    )
                )
                database.insertScene(
                    Scene(
                        "臺北市",
                        "Taipei101",
                        "台北市101大樓",
                        R.drawable.photo0_1,
                        "台北101是超高大樓，是綠建築，是購物中心，是觀景台，更是台灣的指標。"
                    )
                )
                database.insertScene(
                    Scene(
                        "屏東縣",
                        "墾丁",
                        "屏東墾丁國家公園",
                        R.drawable.photo2_0,
                        "墾丁國家公園是台灣在戰後時期第一個成立的國家公園，成立於1982年。"
                    )
                )
                database.insertScene(
                    Scene(
                        "屏東縣",
                        "龍磐公園",
                        "屏東龍磐公園",
                        R.drawable.photo2_1,
                        "未經開發的公園，於開闊的草坪中設有荒野小徑，並坐擁一望無際的海岸風光。"
                    )
                )
            }
        }*/
    fun initDB() {  //setup the initial database
        viewModelScope.launch {
            repeat(1) {
                database.insertScene(
                    Scene(
                        "三星鄉",
                        "味珍香卜肉店",
                        "宜蘭縣三星鄉三星路七段305號",
                        "10:00-17:00  (二)公休",
                        R.drawable.photo10,
                        "卜肉 $270/3~4人份\n酸辣湯 $85/2~三人份",
                        "天送埤  0.2km\n清水地熱  8.2km"
                    )
                )
                database.insertScene(
                    Scene(
                        "頭城鎮",
                        "阿宗芋冰城",
                        "宜蘭縣頭城鎮青雲路三段267號",
                        "09::00-21:00",
                        R.drawable.photo3,
                        "芋頭、紅豆、鳳梨、花生、桂圓、紫米\n三球口味任選 $50",
                        "頭城火車站  0.6km\n蘭陽博物館  1.3km\n外澳沙灘  2.8km"
                    )
                )
                database.insertScene(
                    Scene(
                        "礁溪鄉",
                        "包子饅頭專賣店",
                        "宜蘭縣礁溪鄉中山路一段218號",
                        "13:00-17:00  (日)公休",
                        R.drawable.photo4,
                        "饅頭(限購2顆) $15\n肉包(限購5顆) $20",
                        "礁溪火車站  1km\n湯圍溝公園  1.1km"
                    )
                )
                database.insertScene(
                    Scene(
                        "礁溪鄉",
                        "吳記花生捲冰淇淋",
                        "宜蘭縣礁溪鄉礁溪路四段128號",
                        "10:30-18:00",
                        R.drawable.photo5,
                        "花生捲冰淇淋 $40",
                        "礁溪火車站  1km\n湯圍溝公園  1km"
                    )
                )
                database.insertScene(
                    Scene(
                        "礁溪鄉",
                        "柯氏蔥油餅",
                        "宜蘭縣礁溪鄉礁溪路四段128號",
                        "09:00-18:00",
                        R.drawable.photo6,
                        "蔥油餅 $25\n加蛋 $35",
                        "礁溪火車站  1km\n湯圍溝公園  1km"
                    )
                )
                database.insertScene(
                    Scene(
                        "宜蘭市",
                        "正常鮮肉小籠包",
                        "宜蘭線宜蘭市宜興路一段102號",
                        "06:30-13:00  (一)公休",
                        R.drawable.photo7,
                        "小籠包10個 $80",
                        "幾米公園  0.26km\n宜蘭轉運站  0.35km\n宜蘭火車站  0.5km"
                    )
                )
                database.insertScene(
                    Scene(
                        "羅東鎮",
                        "阿灶伯",
                        "宜蘭縣羅東鎮民權路265號",
                        "16:00-01:00  (三)公休",
                        R.drawable.photo12,
                        "當歸羊肉湯 $75\n臭豆腐 $50",
                        "羅東夜市  0km\n羅東火車站  0.85km"
                    )
                )
                database.insertScene(
                    Scene(
                        "宜蘭市",
                        "王品豆花",
                        "宜蘭縣宜蘭市新民路121號",
                        "09:00-21:30  (日)公休",
                        R.drawable.photo9,
                        "綜合(豆花+粉圓+綠豆) $30\n招牌(豆花+粉圓+花生) $30",
                        "東門夜市  0.5km\n宜蘭火車站  0.55km\n新月廣場  0.65km"
                    )
                )
                database.insertScene(
                    Scene(
                        "礁溪鄉",
                        "春捲伯",
                        "宜蘭縣礁溪鄉育龍路21號",
                        "10:00-17:00  (二)公休",
                        R.drawable.photo13,
                        "春捲 $20\n蝦餅 $25",
                        "龍潭湖  2.1km"
                    )
                )
            }
        }
    }
}