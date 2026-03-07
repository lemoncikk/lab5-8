package org.example.tui.view;

public class ScrollView {
    private final StringBuilder buffer;
    private final int cursor;

    public ScrollView(StringBuilder buffer, int cursor) {
        this.buffer = buffer;
        this.cursor = cursor;
    }

    @Override
    public String toString() {
        int scrollOffset = 0;
        int terminalHeight = 30;
        String[] lines = buffer.toString().split("\n");
        int totalLines = lines.length;
        int cursorLine = 2 + (cursor * 6);
        int visibleStart = scrollOffset;
        int visibleEnd = scrollOffset + terminalHeight - 4;
        if (cursorLine >= visibleEnd) {
            scrollOffset = cursorLine - (terminalHeight / 2);
        }
        else if (cursorLine < visibleStart) {
            scrollOffset = cursorLine - 6;
        }
        scrollOffset = Math.max(0, Math.min(scrollOffset, totalLines - terminalHeight + 4));
        if (scrollOffset < 0) scrollOffset = 0;
        StringBuilder view = new StringBuilder();
        int start = scrollOffset;
        int end = Math.min(start + terminalHeight - 4, totalLines);

        for (int i = start; i < end; i++) {
            view.append(lines[i]).append("\n");
        }
        if (totalLines > terminalHeight - 4) {
            view.append("\n--- ").append(start + 1).append("-").append(end)
                    .append(" из ").append(totalLines).append(" ---");
        }

        return view.toString();
    }
}
