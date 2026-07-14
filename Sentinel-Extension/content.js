const backend_url = "http://localhost:8080/api/scan";
let isBlocked = false;
let timeout = null;
let overrideOption = false;
let lastValue="";
console.log("sentinel started");
function logToTerminal(message, type = "info") {
//   if (!isExtensionContextValid()) return;
  try {
    chrome.storage.local.get(["sg_terminal_logs"], (data) => {
      if (chrome.runtime.lastError) return;
      const logs = data.sg_terminal_logs || [];
      const timestamp = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false });
      logs.push({ timestamp, message, type });
      if (logs.length > 40) logs.shift(); // Keep last 40 logs
      chrome.storage.local.set({ sg_terminal_logs: logs });
    });
  } catch (e) {
    // Ignore runtime context errors
  }
}
function ToggleSendButton(disabled)
{
    document.querySelectorAll("button").forEach(button => {

        const label = (button.innerText || "").toLowerCase();
        const aria = (button.getAttribute("aria-label") || "").toLowerCase();
        if(label.includes("send") || label.includes("submit") || aria.includes("send") || aria.includes("submit"))
        {
            if(disabled){
            button.setAttribute("disabled","true")
            button.style.pointerEvents='none';
            button.onclick = (e)=>{
                e.preventDefault() ; 
                e.stopPropagation();
                return false;
            }
        }
        else
        {
            button.removeAttribute("disabled");
            button.style.pointerEvents="auto";
            button.onclick = null;
        }
        button.style.opacity = disabled ? 0.4 : 1;
        button.style.cursor = disabled ? "not-allowed" : "pointer";
    }
    });
    
}

function detectinput()
{
    
    const editor = 
    document.querySelector('[contenteditable="true"]')|| document.querySelector("textarea");
    if(!editor)
        return;

    const text = (editor.innerText || editor.value || "");

    if(!text || text.length<5)
    {
        if(isBlocked) return;

        lastValue="";
        ToggleSendButton(false)
        return;

    }
    if(text!=lastValue)
    {
        lastValue = text;
        clearTimeout(timeout);

        timeout = setTimeout(async () => {
            logToTerminal(`scanning text change`,"info");

            try{
                const response = await fetch(backend_url,
                    {
                        method : "POST",
                        headers : {"Content-Type" : "application/json"},
                        body :  JSON.stringify({text})
                    }
                );
                if(!response.ok)  throw new Error ("HTTP error" + response.status);
                const result = await response.json();
                console.log("[Sentinel]" ,result);
                handleResult(result);

            } catch (err) {
                logToTerminal(`Text scan failed backend unreachable` + "error");
                console.error("[sentinel] Backend error" , err);
            }
            
            
        },1000);
    }
}

function handleResult(result)
{
    if(result.verdict === "BLOCKED" )
    {
        isBlocked=true;
        ToggleSendButton(true);
    }
    else if(result.verdict === "WARNING")
    {
        isBlocked=true;
        ToggleSendButton(true);

    }
    else
    {
        isBlocked= false;
        ToggleSendButton(false);
        return; 
    }
}  

setInterval(detectinput ,300);
document.addEventListener("input" ,detectinput);
document.addEventListener("keydown",(e)=>
{
    if(isBlocked && e.key === "Enter")
    {
        e.preventDefault();
        e.stopImmediatePropagation();
        e.stopPropagation();
        return false;
    }
},true)