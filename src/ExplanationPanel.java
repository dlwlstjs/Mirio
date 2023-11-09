import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ExplanationPanel extends JPanel {
    private BufferedImage image; // 이미지를 저장할 변수
    private CardLayout cardLayout; //화면 전환
    private JPanel cardPanel; // 화면 전환
    
    public ExplanationPanel(CardLayout cardLayout, JPanel cardPanel) {
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        try {
            // 이미지 파일을 로드합니다. 이미지 파일은 images 폴더에 있어야 합니다.
            image = ImageIO.read(new File("images/explanation.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // 투명한 버튼 생성
        JButton ExplanationButton = new JButton();
        ExplanationButton.setContentAreaFilled(false); // 버튼의 내용 영역을 투명하게 만듭니다
        ExplanationButton.setOpaque(false); // 버튼을 투명하게 만듭니다.
        ExplanationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 화면 전환: StartPanel에서 CharacterPanel로 전환
                cardLayout.show(cardPanel, "RunPanel");
            }
        });

        // 패널에 버튼을 추가
        setLayout(null); // 레이아웃 관리자를 사용하지 않고 직접 위치 설정
        ExplanationButton.setBounds(700, 100, 425, 425); // 버튼의 위치와 크기를 설정
        add(ExplanationButton);
        
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            // 이미지를 패널에 그립니다.
            g.drawImage(image, 0, 0, 1200, 700, null);
        }
    }
}
