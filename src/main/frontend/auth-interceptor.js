console.log("🔥 Auth interceptor loaded");
window.Vaadin = window.Vaadin || {};
window.Vaadin.Flow = window.Vaadin.Flow || {};

const originalSend = window.Vaadin.Flow.send;

window.Vaadin.Flow.send = async function(msg) {

  const token = sessionStorage.getItem("accessToken");
  msg.headers = msg.headers || {};

  if (token) {
    msg.headers["Authorization"] = "Bearer " + token;
  }

  const response = await originalSend.call(this, msg);

  // If unauthorized, try refresh
  if (response && response.status === 401) {
    const refreshed = await refreshAccessToken();

    if (refreshed) {
      msg.headers["Authorization"] = "Bearer " + refreshed;
      return originalSend.call(this, msg); // retry request
    }
  }

  return response;
};

let refreshScheduled = false;

async function refreshAccessToken() {
  try {
    const res = await fetch("/api/auth/refresh", {
      method: "POST",
      credentials: "include"
    });

    if (!res.ok) return null;

    const json = await res.json();
    sessionStorage.setItem("accessToken", json.accessToken);

    scheduleAutoRefresh(json.accessToken);

    return json.accessToken;

  } catch (e) {
    console.error("Refresh failed", e);
    return null;
  }
}

function scheduleAutoRefresh(token) {
  if (!token) return;

  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    const exp = payload.exp * 1000; // JWT expiration in ms
    const now = Date.now();

    const refreshTime = exp - now - 5000; // refresh 5s before expiry

    if (refreshTime > 0) {
      if (refreshScheduled) return;
      refreshScheduled = true;

      setTimeout(() => {
        refreshScheduled = false;
        refreshAccessToken();
      }, refreshTime);
    }

  } catch (e) {
    console.error("Could not schedule token refresh");
  }
}

// Schedule refresh for the current token on page load if exists
const currentToken = sessionStorage.getItem("accessToken");
if (currentToken) {
  scheduleAutoRefresh(currentToken);
}
