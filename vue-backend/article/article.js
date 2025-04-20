
//---------------------------domain---------------------------

function Article(){
    this.id;
    this.title;
    this.content;
    this.author;
}

//---------------------------Mapper---------------------------

function addArticle() {
    var newArticleTitleInput = document.getElementById("newArticleTitle");
    var newArticleContentInput = document.getElementById("newArticleContent");
    var newArticleAuthorInput = document.getElementById("newArticleAuthor");

    var newArticle = {
        title: newArticleTitleInput.value,
        content: newArticleContentInput.value,
        author: newArticleAuthorInput.value
    };

    console.log("添加的Article.title:" + newArticle.title);
    console.log("添加的Article.content:" + newArticle.content);
    console.log("添加的Article.author:" + newArticle.author);

    // 添加文章的逻辑
    var sendAddArticle = {
        data: {
            article: newArticle
        },
        flag: true
    }
    var sendAddArticleJson = JSON.parse(JSON.stringify(sendAddArticle));

    //补全代码,发送给Springboot服务器
    var add_url = basic_url + '/add';
    sendPostRequest(add_url, sendAddArticleJson, addArticleCallback);

    // 清空输入框
    newArticleTitleInput.value = "";
    newArticleContentInput.value = "";
    newArticleAuthorInput.value = "";
}

var selectedDeleteArticles = []; // 在函数外部定义 selectedArticles 变量
function deleteArticle() {
    console.log("deleteArticle 函数被调用");
    var checkboxes = document.querySelectorAll("#articleList input[type='checkbox']:checked");

    selectedDeleteArticles = Array.from(checkboxes).map(function(checkbox) {
        return checkbox.value;
    });

    console.log("selectedDeleteArticles:" + selectedDeleteArticles);

    //存在需要删除的对象
    if(selectedDeleteArticles.length > 0){
        var delete_url = basic_url + '/delete';

        var deleteData = {
            data:{
                deleteList : selectedDeleteArticles
            },
            flag : true
        }
    }

    // 发送 POST 请求给后端
    sendPostRequest(delete_url, deleteData, deleteArticleCallback);

    selectedDeleteArticles = [];
}

function editArticle(updatedValues, articleId) {
    if (updatedValues != null && 
        updatedValues.length > 0) {
        // 遍历 updatedValues 数组
        var editedArticle = {
            id: articleId,
            title : null,
            content: null
        };

        var value = updatedValues[0];
        if (value && value.trim() !== ""){
            editedArticle.title = value;
        }
        else{
            console.log(`第 ${1} 个值为空字符串.`);
            return;
        }
        var value = updatedValues[1];
        if (value && value.trim() !== ""){
            editedArticle.content = value;
        }
        else{
            console.log(`第 ${2} 个值为空字符串.`);
            return;
        }

        console.log(`新文章 ID: ${editedArticle.id}`);
        console.log(`文章标题: ${editedArticle.title}`);
        console.log(`文章内容: ${editedArticle.content}`);
        
        var edit_url = basic_url + "/edit";
        // 添加文章的逻辑
        var sendEditArticle ={
            data:{
                article : editedArticle
            },
            flag : true
        }
        var sendEditArticleJson = JSON.parse(JSON.stringify(sendEditArticle));
        
        sendPostRequest(edit_url,sendEditArticleJson,editArticleCallback);
    } else {
        console.log("无任何更新内容!");
    }
}

function searchArticle() {
    // 获取查询输入框的值
    var searchInput = document.getElementById("searchInput");
    var searchValue = searchInput.value;

    // 查询文章的逻辑
    var search_url = basic_url + '/search';

    // 构造发送给后端的数据对象
    var searchData = {
        data:{
            searchValue: searchValue
        },
        flag : true
    };

    // 发送 POST 请求给后端
    sendPostRequest(search_url, searchData, searchArticleCallback);
}

//---------------------------Request---------------------------

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

//POST 请求
function sendPostRequest(url, data, callback) {
    var xhrPost = new XMLHttpRequest();
    xhrPost.open("POST", url, true);
    xhrPost.setRequestHeader("Content-Type", "application/json");
  
    xhrPost.onreadystatechange = function() {
      if (xhrPost.readyState === 4) {
        if (xhrPost.status === 200) {
          var result = xhrPost.responseText;
          var responseData = JSON.parse(result);
          callback(null, responseData);
        } else {
          var error = "POST request failed with status: " + xhrPost.status;
          callback(error, null);
        }
      }
    };
  
    var jsonData = JSON.stringify(data);
    xhrPost.send(jsonData);
}

var articleData = [];

function getArticleCallback(error, data) {
    if (error) {
      // 处理请求错误
      console.error(error);
    } else {
      // 解析数据
      
      for (var i = 0; i < data.data.length; i++) {
        var articleDataItem = data.data[i];
        var article = new Article();
        article.title = articleDataItem.title;
        article.content = articleDataItem.content;
        article.author = articleDataItem.author;
        article.id = articleDataItem.id;
        articleData.push(article);
      }
      console.log(articleData[0]);

      renderPagination();
    }
}

function addArticleCallback(error, data){
    if (error) {
        // 处理请求错误
        console.error(error);
      } else {
        // 解析数据
        
        if(data.flag === true){
            console.error("添加成功");
        }
        else{
            console.error("请求成功,添加失败");
        }
  
        renderPagination();
      }
}

function searchArticleCallback(error, data){
    if (error) {
        // 处理请求错误
        console.error(error);
      } else {
        // 解析数据
        
        if(data.flag === true){
            if (data.data === null) {
                console.log("数据为空");
                // 清空列表
                articleData = [];
            }
            else{
                
                // 清空列表
                articleData = [];

                // 遍历数据并创建 Article 对象
                data.data.forEach(function (item) {
                    var article = new Article();
                    article.title = item.title;
                    article.content = item.content;
                    article.author = item.author;
                    article.id = item.id;
                    articleData.push(article);
                });

                console.log(articleData);
            }
        }
        else{
            console.error("请求失败");
        }

        // 渲染分页
        renderPagination();
      }
}

function deleteArticleCallback(error, data){
    if (error) {
        // 处理请求错误
        console.error(error);
      } else {
        // 解析数据
        
        if(data.flag === true){
            console.log("删除成功");
        }
        else{
            console.error("删除失败");
        }
        renderPagination();
      }
}

function editArticleCallback(error, data){
    if (error) {
        // 处理请求错误
        console.error(error);
      } else {
        // 解析数据
        
        if(data.flag === true){
            console.log("修改成功");
        }
        else{
            console.error("修改失败");
        }
        renderPagination();
      }
}

//---------------------------Handle---------------------------

//重新绘制
function renderArticleList() {
    var startIndex = (currentPage - 1) * itemsPerPage;
    var endIndex = startIndex + itemsPerPage;
    var paginatedData = articleData.slice(startIndex, endIndex);

    articleList.innerHTML = "";

    paginatedData.forEach(function(article) {
        var listItem = document.createElement("li");

        var checkbox = document.createElement("input");
        checkbox.type = "checkbox";
        checkbox.value = article.id;
        listItem.appendChild(checkbox);

        var nameSpan = document.createElement("span");
        nameSpan.textContent = "文章标题：" + article.title;
        listItem.appendChild(nameSpan);

        var favoriteArticleSpan = document.createElement("span");
        favoriteArticleSpan.textContent = "文章内容：" + article.content;
        listItem.appendChild(favoriteArticleSpan);

        var articleReviewSpan = document.createElement("span");
        articleReviewSpan.textContent = "文章作者：" + article.author;
        listItem.appendChild(articleReviewSpan);

        var updateButton = document.createElement("button");
        updateButton.textContent = "更新";
        updateButton.onclick = function() {
            toggleUpdateContainer(listItem);
        };
        listItem.appendChild(updateButton);

        var updateContainer = document.createElement("div");
        updateContainer.className = "update-container";
        updateContainer.id = `update-container-${article.id}`;//用于定位是哪一个
        // 创建输入框并设置提示文字

        var inputElement1 = document.createElement("input");
        inputElement1.type = "text";
        inputElement1.className = "update-input";
        inputElement1.placeholder = "输入文章标题";

        var inputElement2 = document.createElement("input");
        inputElement2.type = "text";
        inputElement2.className = "update-input";
        inputElement2.placeholder = "输入文章内容";

        updateContainer.appendChild(inputElement1);
        updateContainer.appendChild(inputElement2);

        updateInputs = document.createDocumentFragment();
        listItem.appendChild(updateContainer);

        var confirmButton = document.createElement("button");
        confirmButton.textContent = "确定更新";
        confirmButton.onclick = function() {
            // 获取 updateContainer 中的 update-input 输入框的值
            var updateInputElements = updateContainer.querySelectorAll(".update-input");
            var updatedValues = [];
            updateInputElements.forEach(function(inputElement) {
                updatedValues.push(inputElement.value);
            });
            editArticle(updatedValues, article.id);
            toggleUpdateContainer(listItem);
        };
        updateContainer.appendChild(confirmButton);

        articleList.appendChild(listItem);
    });
}

//生成输入框
function createUpdateInputs(article) {
    var updateInputs = document.createDocumentFragment();

    for (var key in article) {
        if (article.hasOwnProperty(key) && key !== "name" && key !== "id") {
            var input = document.createElement("input");
            input.type = "text";
            input.className = "update-input";
            updateInputs.appendChild(input);
        }
    }

    return updateInputs;
}

//切换更新按钮的状态
function toggleUpdateContainer(listItem) {
    var updateContainer = listItem.querySelector(".update-container");
    var updateButton = listItem.querySelector("button");

    updateContainer.style.display = updateContainer.style.display === "none" ? "block" : "none";
    updateButton.textContent = updateContainer.style.display === "none" ? "更新" : "取消更新";
}

//全选
function toggleSelectAll() {
    var checkboxes = document.querySelectorAll("#articleList input[type='checkbox']");
    var selectAllCheckbox = document.getElementById("selectAll");

    checkboxes.forEach(function(checkbox) {
        checkbox.checked = selectAllCheckbox.checked;
    });
}

//分页
function renderPagination() {
    var totalPages = Math.ceil(articleData.length / itemsPerPage);
  
    paginationContainer.innerHTML = "";
  
    // 上一页按钮
    var previousButton = document.createElement("button");
    previousButton.textContent = "上一页";
    previousButton.className = "pagination-button";
    previousButton.onclick = function () {
      if (currentPage > 1) {
        currentPage--;
        renderArticleList();
        updateCurrentPageSpan(); // 更新当前页数显示
      }
    };
    paginationContainer.appendChild(previousButton);
  
    // 当前页数显示函数
    function updateCurrentPageSpan() {
      currentPageSpan.textContent = "当前页：" + currentPage;
    }
  
    // 当前页数
    var currentPageSpan = document.createElement("span");
    updateCurrentPageSpan();
    paginationContainer.appendChild(currentPageSpan);
  
    // 下一页按钮
    var nextButton = document.createElement("button");
    nextButton.textContent = "下一页";
    nextButton.className = "pagination-button";
    nextButton.onclick = function () {
      if (currentPage < totalPages) {
        currentPage++;
        renderArticleList();
        updateCurrentPageSpan(); // 更新当前页数显示
      }
    };
    paginationContainer.appendChild(nextButton);
  
    // 最后一页按钮
    var lastPageButton = document.createElement("button");
    lastPageButton.textContent = "最后一页";
    lastPageButton.className = "pagination-button";
    lastPageButton.onclick = function () {
      currentPage = totalPages;
      renderArticleList();
      updateCurrentPageSpan(); // 更新当前页数显示
    };
    paginationContainer.appendChild(lastPageButton);
  
    // 总页数显示
    var totalPagesSpan = document.createElement("span");
    totalPagesSpan.textContent = " / 总页数：" + totalPages;
    paginationContainer.appendChild(totalPagesSpan);
  
    // 跳转到某一页的输入框
    var jumpInput = document.createElement("input");
    jumpInput.type = "number";
    jumpInput.min = 1;
    jumpInput.max = totalPages;
    jumpInput.value = currentPage;
    paginationContainer.appendChild(jumpInput);
  
    // 跳转按钮
    var jumpButton = document.createElement("button");
    jumpButton.textContent = "跳转";
    jumpButton.className = "pagination-button";
    jumpButton.onclick = function () {
      var jumpPage = parseInt(jumpInput.value);
      if (jumpPage >= 1 && jumpPage <= totalPages) {
        currentPage = jumpPage;
        renderArticleList();
        updateCurrentPageSpan(); // 更新当前页数显示
      }
    };
    paginationContainer.appendChild(jumpButton);
  
    renderArticleList();
}

    

//---------------------------Init---------------------------

var articleList = document.getElementById("articleList");
var paginationContainer = document.getElementById("paginationContainer");
var itemsPerPage = 6; // 每页显示的信息条数
var currentPage = 1; // 当前页码
var basic_url = 'http://127.0.0.1:8080/article';

function initMain(){
    // 使用 sendGetRequest 函数并传递回调函数作为参数
    var init_url = basic_url + '/all/1';
    sendGetRequest(init_url, getArticleCallback);
}

//---------------------------Main---------------------------

function Main(){
    initMain();
    renderArticleList();
}


Main();




