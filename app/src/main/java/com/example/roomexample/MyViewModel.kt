package com.example.roomexample


import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.roomexample.database.Scene
import com.example.roomexample.database.SceneDatabase
import com.example.roomexample.weather.City
import com.example.roomexample.weather.CityWeather
import com.example.roomexample.weather.GetService
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


    val cities = listOf(
        City("Yilan", "宜蘭"),
        City("Luodong ", "羅東"),
        City("Su-ao","蘇澳"),
        City("Toucheng","頭城"),
        City("Jiaosi","礁溪"),
        City("Jhuanwei","壯圍"),
        City("Yuanshan","員山"),
        City("Dongshan","冬山"),
        City("Wujie","五結"),
        City("Sensing","三星"),
        City("Datong","大同"),
        City("Nan-ao","南澳")
    )

    //extract city names into two seperated lists
    val cities_ch = MutableList(cities.size) { cities[it].cName }
    val cities_en = MutableList(cities.size) { cities[it].eName }

    //add the selection hint text to be the first item
    init {
        cities_ch.add(0, "選擇一個鄉鎮")
        cities_en.add(0, "Select one city")
    }

    //livedata
    val selectedCityWeather = MutableLiveData<CityWeather>()

    //Coroutine version
    fun sendRetrofitRequest(location: String) {
        viewModelScope.launch {
            try {
                val result =
                    GetService.retrofitService.getAppData(location, "metric", "zh_tw", API_KEY)
                val temp = CityWeather(
                    result.name,
                    result.main.temp,
                    result.weather[0].description,
                    result.weather[0].icon
                )
                selectedCityWeather.value = temp
                Log.d("Main", temp.toString())
            } catch (e: Exception) {
                Log.d("Main", "Fail to access: ${e.message}")
            }
        }
    }

    companion object {  //static global constants
        const val API_URL = "https://api.openweathermap.org/"
        const val ICON_URL = "https://openweathermap.org/img/wn/"
        const val API_KEY = "843b0a7691567bf32812c9c01939d921"
    }

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