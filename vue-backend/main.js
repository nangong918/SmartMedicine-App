// 获取输入框和登录按钮
const usernameInput = document.querySelector('input[name="username"]');
const passwordInput = document.querySelector('input[name="password"]');
const loginBtn = document.getElementById('login-btn');

// 添加登录按钮的点击事件监听器
loginBtn.addEventListener('click', () => {
  const username = usernameInput.value;
  const password = passwordInput.value;

  // 检查用户名和密码是否正确
  if (username === 'Admin' && password === '123') {
    // 跳转到 home.html 页面
    window.location.href = 'home/home.html';
  } else {
    // 显示错误消息
    alert('Invalid username or password');
  }
});