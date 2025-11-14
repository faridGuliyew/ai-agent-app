# 📘 AI-Powered Notes App

An Android notes application enhanced with a local LLM (Ollama) that understands natural language commands and converts them into **structured JSON actions**.  
Users can manage notes by simply talking to the AI, while the app safely interprets responses through a strict JSON contract.

---

## 🚀 Features

### 🧠 AI-Driven Note Management
Example user commands:
- “Create a note called Shopping List”
- “Rename ‘Work Plan’ to ‘Q4 Strategy’”
- “Delete the note My First Note”
- “Mark Today’s Journal as favorite”
- “Update the content of Travel Ideas”

AI outputs **only JSON**, like:

```json
{
  "actions": [
    {
      "event": "MODIFY_NOTE",
      "data": {
        "currentTitle": "My first note",
        "newTitle": null,
        "newContent": null,
        "isFavorite": null,
        "operationType": "DELETE"
      }
    }
  ],
  "failReason": null
}
```

✔ Guaranteed Safe Parsing
📱 Modern Android App

Jetpack Compose UI
Chat interface with typing animation
Local LLM communication
Natural language commands for notes
Clean, modern UX

🔗 Local LLM (Ollama) Integration
The app sends prompts to a running Ollama instance via Ktor:
client.post("http://host:port/api/generate") { ... }


🏗 Running the App
1. Install Ollama
https://ollama.com/download

3. Create the custom model
ollama create <model-name> -f ./model.mod

5. Start the server
ollama run <model-name>

6. Run the Android app
Build & run from Android Studio.
Point the client to your machine:
