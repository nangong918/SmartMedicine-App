//---------------------------domain---------------------------

function Home(){
  this.user_num;
  this.article_num;
  this.author_num;
}

//---------------------------Handle---------------------------

//重新绘制
function render() {
  // 将HomeData的值更新给Count组件
  document.getElementById("userCount").textContent = HomeData.length > 0 ? HomeData[0] : 0;
  document.getElementById("articleCount").textContent = HomeData.length > 0 ? HomeData[1] : 0;
  document.getElementById("authorCount").textContent = HomeData.length > 0 ? HomeData[2] : 0;
}

function navigateToPage(url) {
  window.location.href = url;
}

//---------------------------Request---------------------------

HomeData = [];
//GET 请求
function sendGetRequest(url, callback) {
  var xhrGet = new XMLHttpRequest();
  xhrGet.open("GET", url, true);

  xhrGet.onreadystatechange = function() {
    if (xhrGet.readyState === 4) {
      if (xhrGet.status === 200) {
        var result = xhrGet.responseText;
        var data = JSON.parse(result);
        callback(null, data);
      } else {
        var error = "GET request failed with status: " + xhrGet.status;
        callback(error, null);
      }
    }
  };

  xhrGet.send();
}

function checkHomeCallback(error, data){
    if (error) {
      // 处理请求错误
      console.error(error);
    } 
    else {
      // 解析数据
      if(data.flag === true){
          if (data.data === null) {
              console.log("数据为空");
              // 清空列表
              HomeData = [];
          }
          else{
              
              // 清空列表
              HomeData = [];

              // 遍历数据并创建 Author 对象
              HomeData.push(data.data.user_num);
              HomeData.push(data.data.article_num);
              HomeData.push(data.data.author_num);

              console.log(HomeData);
          }
      }
      else{
          console.error("请求失败");
      }

      // 渲染分页
      render();
    }
}

//---------------------------Init---------------------------

var basic_url = 'http://127.0.0.1:8080/user_recommend';

function setOnClickListener(){
    var userCountElement = document.querySelector('.m_list.m_1');
    userCountElement.addEventListener("click", function() {
      navigateToPage("../user/user.html");
    });
    var articleCountElement = document.querySelector('.m_list.m_2');
    articleCountElement.addEventListener("click", function() {
      navigateToPage("../article/article.html");
    });
    var authorCountElement = document.querySelector('.m_list.m_3');
    authorCountElement.addEventListener("click", function() {
      navigateToPage("../author/author.html");
    });
}

function setElement(){
  // 将数量赋值给对应的 HTML 元素
  document.getElementById("userCount").textContent = 0;
  document.getElementById("articleCount").textContent = 0;
  document.getElementById("authorCount").textContent = 0;
}

function initMain(){
    setElement();
    setOnClickListener();
    var init_url = basic_url + '/all/1';
    sendGetRequest(init_url, checkHomeCallback);
}

//---------------------------Main---------------------------

function Main(){
    initMain();
    render();
}


Main();