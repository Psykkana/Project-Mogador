public class DialogueManager {
    private String[] pages;
    private int currentPageIndex;
    private boolean isActive;
    private String currentSpeaker;

    public void startDialogue(String[] dialogue, String speaker) {
        this.pages = dialogue;
        this.currentSpeaker = speaker;
        this.currentPageIndex = 0;
        this.isActive = true;
    }

    public String getCurrentPage() {
        if (pages == null || currentPageIndex >= pages.length) return "";
        return pages[currentPageIndex];
    }

    public void next() {
        currentPageIndex++;
        if (currentPageIndex >= pages.length) {
            isActive = false;
        }
    }

    // Getters
    public boolean isActive() { 
        return isActive; 
    }

    public String getSpeaker() { 
        return currentSpeaker; 
    }

    public int getPageIndex() { 
        return currentPageIndex; 
    }
}