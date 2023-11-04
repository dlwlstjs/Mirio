import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.border.Border;

public class RunPanel extends JPanel {
	private CardLayout cardLayout; //화면 전환
    private JPanel cardPanel; // 화면 전환
    
    private int x = 70; // 플레이어의 x 좌표
    private int y = 600; // 플레이어의 y 좌표
    private Image backgroundImage; // 배경 이미지를 저장할 Image 변수
    private Image player; // 캐릭터 이미지를 저장할 Image 변수
    private String playerU; // 이미지 경로 저장할 변수
    private String playerD; // 이미지 경로 저장할 변수

    private boolean isRight; // 오른쪽 방향 키가 눌렸는지 확인
    private boolean isLeft; // 왼쪽 방향 키가 눌렸는지 확인
    private boolean isJump; // 점프 중인지 확인

    private static final int JUMPSPEED = 2; // 점프 속도
    private static final int SPEED = 3; // 이동 속도
    
    
    
    public void setCharacterImage(String characterSelection) {
        this.playerD = characterSelection+ ".png";
        playerU = characterSelection+ "_up.png";;
        player = new ImageIcon(playerD).getImage();
        
    }
   

    public RunPanel(CardLayout cardLayout, JPanel cardPanel) {
    	this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        setFocusable(true); // 패널이 포커스를 받을 수 있도록 함
       // setPreferredSize(new Dimension(1000, 640)); // 패널의 크기 설정
        
        // 배경 이미지 로드
        backgroundImage = new ImageIcon("images/run.png").getImage();
        
        
        // 투명한 버튼 생성
        JButton runbtn = new JButton();
        runbtn.setContentAreaFilled(false); // 버튼의 내용 영역을 투명하게 만듭니다
        runbtn.setOpaque(false); // 버튼을 투명하게 만듭니다.
        runbtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	// 화면 전환: StartPanel에서 CharacterPanel로 전환
            	cardLayout.show(cardPanel, "StudyPanel");
            }
        });
        setLayout(null); // 레이아웃 관리자를 사용하지 않고 직접 위치 설정
        runbtn.setBounds(700, 100, 425, 425); // 버튼의 위치와 크기를 설정
        add(runbtn); // 패널에 버튼을 추가
        
        // 진행바
        JProgressBar progressBar = new JProgressBar(0, 60);
        progressBar.setBounds(20, 20, 1145, 25);
        progressBar.setValue(60);
        progressBar.setForeground(Color.PINK); // Set progress bar color to pink
        Color customYellow = new Color(254, 239, 197); // RGB values for #FEEFC5
        progressBar.setBackground(customYellow); // Set progress bar background color to #FEEFC5
        add(progressBar);

        Timer timer = new Timer(1000, new ActionListener() {
            int timeInSeconds = 60;

            @Override
            public void actionPerformed(ActionEvent e) {
                timeInSeconds--;
                progressBar.setValue(timeInSeconds);

                if (timeInSeconds <= 0) {
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        timer.start();

        

        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        // 오른쪽 키
        KeyStroke rightKeyReleased = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false);
        inputMap.put(rightKeyReleased, "RightReleased");
        actionMap.put("RightReleased", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                isRight = true;
                moveRight();
            }
        });

        KeyStroke rightKeyPressed = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true);
        inputMap.put(rightKeyPressed, "RightPressed");
        actionMap.put("RightPressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                isRight = false;
            }
        });

        // 왼쪽 키
        KeyStroke leftKeyReleased = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false);
        inputMap.put(leftKeyReleased, "LeftReleased");
        actionMap.put("LeftReleased", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                isLeft = true;
                moveLeft();
            }
        });

        KeyStroke leftKeyPressed = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true);
        inputMap.put(leftKeyPressed, "LeftPressed");
        actionMap.put("LeftPressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                isLeft = false;
            }
        });

        // 위쪽 키
        KeyStroke upKey = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false);
        inputMap.put(upKey, "Up");
        actionMap.put("Up", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!isJump) {
                    isJump = true;
                    jump();
                }
            }
        });
    }

    private void moveRight() {
        new Thread(() -> {
            while (isRight) {
                x += SPEED; // 이동 속도를 적용하여 x 값을 변경
                setPlayerLocation();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void moveLeft() {
        new Thread(() -> {
            while (isLeft) {
                x -= SPEED;
                setPlayerLocation();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void jump() {
        isJump = true;
        
        // 플레이어 이미지 변경
        player = new ImageIcon(playerU).getImage();
        
        new Thread(() -> {
            for (int i = 0; i < 130 / JUMPSPEED; i++) {
                y -= JUMPSPEED;
                setPlayerLocation();
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < 130 / JUMPSPEED; i++) {
                y += JUMPSPEED;
                setPlayerLocation();
                try {
                    Thread.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            isJump = false;
            // 스윙 스레드에서 플레이어 이미지를 기본 이미지로 변경
            SwingUtilities.invokeLater(() -> {
                player = new ImageIcon(playerD).getImage();
                repaint();
            });
        }).start();
    }

    private void setPlayerLocation() {
        SwingUtilities.invokeLater(() -> {
            repaint();
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // 배경 이미지 그리기
        g.drawImage(backgroundImage, 0, 0, this);

        // 캐릭터 이미지 그리기
        g.drawImage(player, x, y, this);
    }


}
