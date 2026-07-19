const backend_url :string = "http://localhost:8080/api/scan";
let isBlocked :boolean = false;
let timeout : ReturnType<typeof setTimeout> |undefined=undefined ;
let overrideOption :boolean= false;
let lastValue:string="";
console.log("sentinel started");
function logToTerminal(
    message:string, 
    type:string = "info"):void {
//   if (!isExtensionContextValid()) return;
  try {
    chrome.storage.local.get(
        ["sg_terminal_logs"], 
        (data:any) => {
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
function ToggleSendButton(disabled :boolean) :void
{
    document.querySelectorAll<HTMLButtonElement>("button").forEach(button => {

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
        button.style.opacity = disabled ? "0.4" : "1";
        button.style.cursor = disabled ? "not-allowed" : "pointer";
    }
    });
    
}

function detectinput()
{
    
    const editor = 
    (document.querySelector('[contenteditable="true"]')?? document.querySelector("textarea") )as HTMLElement | HTMLTextAreaElement | null;
    if(!editor)
        return;

    // ?? checks for only null and undefined || checks for all falsy values (like 0 nan null undefined)
    const text = editor instanceof HTMLTextAreaElement ? editor.value : editor.innerText || "";

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
                const result : any = await response.json();
                console.log("[Sentinel]" ,result);
                handleResult(result);

            } catch (err:unknown) {
                logToTerminal(`Text scan failed backend unreachable` , "error");
                console.error("[sentinel] Backend error" , err);
            }
            
            
        },1000);
    }
}

function handleResult(result:any)
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

function createBanner(message:any, type="warning")
{
    removeBanner();
    const banner = document.createElement("div");
    banner.id = "Sentinel-Banner";
    const icon = type ==="blocked" ? "🚫" : "⚠️";
    const title = type ==="blocked"?"Blocked":"Warning";

    document.body.appendChild(banner);

    banner.innerHTML=`
    <button class="sg-banner-close" id="sg-banner-close-btn">✕</button>
    <div class="sg-banner-header">
      Sentinel — ${title}
    </div>
    <div class="sg-banner-reasons">${message}</div>
    `

    const closebtn = document.getElementById("sg-banner-close-btn");
    if(closebtn) 
    {
        closebtn.addEventListener("click",()=>
        {
            banner.remove();
        })
    }
}

function removeBanner()
{
    const old = document.getElementById("Sentinel-Banner");
    if(old) 
    {
        old.remove();
    }
}