const backend_url = "http://localhost:8080/api/scan";
let isBlocked = false;
let timeout = null;
let overrideOption = false;
let lastValue=''


function ToggleSendButton(disabled)
{
    document.querySelector("button").forEach(button => {

        const label = (button.innerText || "").toLowerCase();
        const aria = (button.getAttribute("aria-label") || "").toLowerCase();
        if(label.includes("send") || label.includes("submit") || aria.includes("send") || aria.includes("submit"))
        {
            if(disabled){
            button.setAttribute("disabled",true)
            button.style.pointerEvents='none';
            button.onclick = (e)=>{
                e.preventDefault() ; 
                e.stopPropogation();
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
    const editor = document.querySelector("textarea");

    const text = (editor.innerText || editor.value || "");

    if(!text || text.length<5)
    {
        if(isBlocked) return;

        lastValue="";
        ToggleSendButton(false)

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
                        body :  JSON.stringify(text)
                    }
                );
                if(!response.ok)  throw new Error ("HTTP error" + response.status);
                const result = await response.json();
                console.log("[Sentinel]" ,result);

            } catch (err) {
                logToTerminal(`Text scan failed backend unreachable` + "error");
                console.error("[sentinel] Backend error" , err);
            }
            
        })
    }


}

