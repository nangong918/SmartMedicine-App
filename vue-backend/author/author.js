
//---------------------------domain---------------------------

function Author(){
    this.id;
    this.name;
}

//---------------------------Mapper---------------------------

function addAuthor() {
    var newAuthorNameInput = document.getElementById("newAuthorName");

    var newAuthor = {
        name: newAuthorNameInput.value
    };

    console.log("添加的Author.name:" + newAuthor.name);

    // 添加作者的逻辑
    var sendAddAuthor = {
        data: {
            author: newAuthor
        },
        flag: true
    }
    var sendAddAuthorJson = JSON.parse(JSON.stringify(sendAddAuthor));

    //补全代码,发送给Springboot服务器
    var add_url = basic_url + '/add';
    sendPostRequest(add_url, sendAddAuthorJson, addAuthorCallback);

    // 清空输入框
    newAuthorNameInput.value = "";
}

var selectedDeleteAuthors = []; // 在函数外部定义 selectedAuthors 变量
function deleteAuthor() {
    console.log("deleteAuthor 函数被调用");
    var checkboxes = document.querySelectorAll("#authorList input[type='checkbox']:checked");

    selectedDeleteAuthors = Array.from(checkboxes).map(function(checkbox) {
        return checkbox.value;
    });

    console.log("selectedDeleteAuthors:" + selectedDeleteAuthors);

    //存在需要删除的对象
    if(selectedDeleteAuthors.length > 0){
        var delete_url = basic_url + '/delete';

        var deleteData = {
            data:{
                deleteList : selectedDeleteAuthors
            },
            flag : true
        }
    }

    // 发送 POST 请求给后端
    sendPostRequest(delete_url, deleteData, deleteAuthorCallback);

    selectedDeleteAuthors = [];
}

function editAuthor(updatedValues, authorId) {
    if (updatedValues != null && 
        updatedValues.length > 0) {
        // 遍历 updatedValues 数组
        var editedAuthor = {
            id: authorId,
            name : null
        };

        var value = updatedValues[0];
        if (value && value.trim() !== ""){
            editedAuthor.name = value;
        }
        else{
            console.log(`第 ${1} 个值为空字符串.`);
            return;
        }

        console.log(`新作者 ID: ${editedAuthor.id}`);
        console.log(`作者姓名: ${editedAuthor.name}`);
        
        var edit_url = basic_url + "/edit";
        // 添加作者的逻辑
        var sendEditAuthor ={
            data:{
                author : editedAuthor
            },
            flag : true
        }
        var sendEditAuthorJson = JSON.parse(JSON.stringify(sendEditAuthor));
        
        sendPostRequest(edit_url,sendEditAuthorJson,editAuthorCallback);
    } else {
        console.log("无任何更新内容!");
    }
}

function searchAuthor() {
    // 获取查询输入框的值
    var searchInput = document.getElementById("searchInput");
    var searchValue = searchInput.value;

    // 查询作者的逻辑
    var search_url = basic_url + '/search';

    // 构造发送给后端的数据对象
    var searchData = {
        data:{
            searchValue: searchValue
        },
        flag : true
    };

    // 发送 POST 请求给后端
    sendPostRequest(search_url, searchData, searchAuthorCallback);
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

var authorData = [];

function getAuthorCallback(error, data) {
    if (error) {
      // 处理请求错误
      console.error(error);
    } else {
      // 解析数据
      for (var i = 0; i < data.data.length; i++) {
        var authorDataItem = data.data[i];
        var author = new Author();
        author.name = authorDataItem.name;
        author.id = authorDataItem.id;
        authorData.push(author);
      }
      console.log(authorData[0]);

      renderPagination();
    }
}

function addAuthorCallback(error, data){
    if (error) {
        // 处理请求错误
        console.error(error);
      } else {
        // 解析数据
        
        if(data.flag === true){
            console.log("添加成功");
        }
        else{
            console.error("请求成功,添加失败");
        }
  
        renderPagination();
      }
}

function searchAuthorCallback(error, data){
    if (error) {
        // 处理请求错误
        console.error(error);
      } else {
        // 解析数据
        
        if(data.flag === true){
            if (data.data === null) {
                console.log("数据为空");
                // 清空列表
                authorData = [];
            }
            else{
                
                // 清空列表
                authorData = [];

                // 遍历数据并创建 Author 对象
                data.data.forEach(function (item) {
                    var author = new Author();
                    author.name = item.name;
                    author.id = item.id;
                    authorData.push(author);
                });

                console.log(authorData);
            }
        }
        else{
            console.error("请求失败");
        }

        // 渲染分页
        renderPagination();
      }
}

function deleteAuthorCallback(error, data){
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

function editAuthorCallback(error, data){
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
function renderAuthorList() {
    var startIndex = (currentPage - 1) * itemsPerPage;
    var endIndex = startIndex + itemsPerPage;
    var paginatedData = authorData.slice(startIndex, endIndex);

    authorList.innerHTML = "";

    paginatedData.forEach(function(author) {
        var listItem = document.createElement("li");

        var checkbox = document.createElement("input");
        checkbox.type = "checkbox";
        checkbox.value = author.id;
        listItem.appendChild(checkbox);

        var nameSpan = document.createElement("span");
        nameSpan.textContent = "作者名称：" + author.name;
        listItem.appendChild(nameSpan);

        var updateButton = document.createElement("button");
        updateButton.textContent = "更新";
        updateButton.onclick = function() {
            toggleUpdateContainer(listItem);
        };
        listItem.appendChild(updateButton);

        var updateContainer = document.createElement("div");
        updateContainer.className = "update-container";
        updateContainer.id = `update-container-${author.id}`;//用于定位是哪一个
        // 创建输入框并设置提示文字

        var inputElement1 = document.createElement("input");
        inputElement1.type = "text";
        inputElement1.className = "update-input";
        inputElement1.placeholder = "输入作者名称";

        updateContainer.appendChild(inputElement1);

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
            editAuthor(updatedValues, author.id);
            toggleUpdateContainer(listItem);
        };
        updateContainer.appendChild(confirmButton);

        authorList.appendChild(listItem);
    });
}

//生成输入框
function createUpdateInputs(author) {
    var updateInputs = document.createDocumentFragment();

    for (var key in author) {
        if (author.hasOwnProperty(key) && key !== "name" && key !== "id") {
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
    var checkboxes = document.querySelectorAll("#authorList input[type='checkbox']");
    var selectAllCheckbox = document.getElementById("selectAll");

    checkboxes.forEach(function(checkbox) {
        checkbox.checked = selectAllCheckbox.checked;
    });
}

//分页
function renderPagination() {
    var totalPages = Math.ceil(authorData.length / itemsPerPage);
  
    paginationContainer.innerHTML = "";
  
    // 上一页按钮
    var previousButton = document.createElement("button");
    previousButton.textContent = "上一页";
    previousButton.className = "pagination-button";
    previousButton.onclick = function () {
      if (currentPage > 1) {
        currentPage--;
        renderAuthorList();
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
        renderAuthorList();
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
      renderAuthorList();
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
        renderAuthorList();
        updateCurrentPageSpan(); // 更新当前页数显示
      }
    };
    paginationContainer.appendChild(jumpButton);
  
    renderAuthorList();
}

//---------------------------Init---------------------------

var authorList = document.getElementById("authorList");
var paginationContainer = document.getElementById("paginationContainer");
var itemsPerPage = 6; // 每页显示的信息条数
var currentPage = 1; // 当前页码
var basic_url = 'http://127.0.0.1:8080/author';

function initMain(){
    // 使用 sendGetRequest 函数并传递回调函数作为参数
    var init_url = basic_url + '/all/1';
    sendGetRequest(init_url, getAuthorCallback);
}

//---------------------------Main---------------------------

function Main(){
    initMain();
    renderAuthorList();
}


Main();

