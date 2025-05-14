import javax.swing.;
import java.awt.;
import java.util.concurrent.locks.ReentrantLock; 

public class SelectionSortAnimation extends JComponent { private int[] array; private int sortedIndex = -1; // Index up to which array is sorted private int markedIndex = -1; // Currently inspected element private final ReentrantLock lock = new ReentrantLock(); 

private static final int DELAY = 300; // Pause duration in milliseconds 
 
public SelectionSortAnimation(int size, int maxValue) { 
    array = new int[size]; 
    for (int i = 0; i < size; i++) { 
        array[i] = (int) (Math.random() * maxValue) + 10; 
    } 
    setPreferredSize(new Dimension(600, 400)); 
} 
 
@Override 
protected void paintComponent(Graphics g) { 
    super.paintComponent(g); 
    lock.lock(); 
    try { 
        Graphics2D g2 = (Graphics2D) g; 
        int width = getWidth(); 
        int height = getHeight(); 
        int barWidth = width / array.length; 
 
        for (int i = 0; i < array.length; i++) { 
            if (i <= sortedIndex) { 
                g2.setColor(Color.BLUE); // Sorted portion 
            } else if (i == markedIndex) { 
                g2.setColor(Color.RED);  // Element currently inspected 
            } else { 
                g2.setColor(Color.BLACK); // Unsorted portion 
            } 
            int barHeight = array[i]; 
            g2.fillRect(i * barWidth, height - barHeight, barWidth - 2, barHeight); 
        } 
    } finally { 
        lock.unlock(); 
    } 
} 
 
public void startAnimation() { 
    Thread sortingThread = new Thread(() -> { 
        try { 
            selectionSort(); 
        } catch (InterruptedException e) { 
            Thread.currentThread().interrupt(); 
        } 
    }); 
    sortingThread.start(); 
} 
 
private void selectionSort() throws InterruptedException { 
    for (int i = 0; i < array.length - 1; i++) { 
        int minIndex = i; 
 
        lock.lock(); 
        try { 
            sortedIndex = i - 1;  // Mark sorted portion 
        } finally { 
            lock.unlock(); 
        } 
 
        for (int j = i + 1; j < array.length; j++) { 
            lock.lock(); 
            try { 
                markedIndex = j;  // Mark current inspected element 
            } finally { 
                lock.unlock(); 
            } 
            repaint(); 
            Thread.sleep(DELAY); 
 
            lock.lock(); 
            try { 
                if (array[j] < array[minIndex]) { 
                    minIndex = j; 
                } 
            } finally { 
                lock.unlock(); 
            } 
        } 
 
        // Swap the smallest found element with the ith element 
        lock.lock(); 
        try { 
            int temp = array[i]; 
            array[i] = array[minIndex]; 
            array[minIndex] = temp; 
 
            sortedIndex = i;  // Update sorted portion 
            markedIndex = -1; // Clear marked element 
        } finally { 
            lock.unlock(); 
        } 
        repaint(); 
        Thread.sleep(DELAY); 
    } 
 
    // Mark entire array as sorted at the end 
    lock.lock(); 
    try { 
        sortedIndex = array.length - 1; 
        markedIndex = -1; 
    } finally { 
        lock.unlock(); 
    } 
    repaint(); 
} 
 
public static void main(String[] args) { 
    JFrame frame = new JFrame("Selection Sort Animation"); 
    SelectionSortAnimation animation = new SelectionSortAnimation(30, 300); 
 
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
    frame.add(animation); 
    frame.pack(); 
    frame.setLocationRelativeTo(null); 
    frame.setVisible(true); 
 
    animation.startAnimation(); 
} 
  

} 

 

 