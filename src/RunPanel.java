import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.border.Border;
import java.util.ArrayList;
import java.util.List;

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
    JProgressBar progressBar = new JProgressBar(0, 100); //진행바
    //private volatile boolean isRight = true; // isRight가 제어 변수라고 가정합니다
    private volatile boolean workerRunning = false; // SwingWorker가 실행 중인지를 추적하는 변수
    private SwingWorker<Void, Void> worker; // 메서드 바깥에 SwingWorker 선언
    
    private List<Block> blocks; // 블록 객체를 보관할 List
    
    
    //CharacterPanel에서 매개변수 받아옴
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
        progressBar.setBounds(20, 20, 1145, 25);
        progressBar.setValue(100);
        progressBar.setForeground(Color.PINK);
        Color customYellow = new Color(254, 239, 197);
        progressBar.setBackground(customYellow);
        add(progressBar);

        // 생성자 또는 초기화 블록 내부에서 SwingWorker를 초기화합니다
        worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (int i = 100; i >= 0; i--) {
                    progressBar.setValue(i);
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                workerRunning = false; // SwingWorker가 작업을 완료하면 플래그를 재설정합니다
                return null;
            }
        };
        

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
        
        
     
        blocks = new ArrayList<>(); // 블록들을 담을 리스트 초기화
        
        // 블록 추가 - 게임에 맞게 값을 조정해주세요
        blocks.add(new Block(200, 650, 100, 20)); // 예시 블록 - y값을 550으로 변경하여 블록을 낮춤
        blocks.add(new Block(400, 600, 80, 20)); // 예시 블록 - y값을 500으로 변경하여 블록을 낮춤
        blocks.add(new Block(600, 550, 100, 20)); // 예시 블록 - y값을 550으로 변경하여 블록을 낮춤
        blocks.add(new Block(800, 500, 80, 20)); // 예시 블록 - y값을 500으로 변경하여 블록을 낮춤
        
        
        // 패널에 키 리스너를 추가하여 키 이벤트를 처리
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                handleKeyRelease(e);
            }
        });

        this.setFocusable(true);
        this.requestFocusInWindow();

        
    }// RunPanel

    private void moveRight() {
        new Thread(() -> {
            while (isRight) {
                boolean canMove = true;
                // 플레이어가 우측으로 이동할 때 충돌 확인
                for (Block block : blocks) {
                    if (x + player.getWidth(null) > block.getX() &&
                        x + player.getWidth(null) < block.getX() + block.getWidth() &&
                        y + player.getHeight(null) > block.getY() &&
                        y < block.getY() + block.getHeight()) {
                        canMove = false;
                        break;
                    }
                }

                if (canMove) {
                    x += SPEED; // 이동 속도를 적용하여 x 값을 변경합니다
                    setPlayerLocation();
                }

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
                boolean canMove = true;
                // 플레이어가 좌측으로 이동할 때 충돌 확인
                for (Block block : blocks) {
                    if (x > block.getX() &&
                        x < block.getX() + block.getWidth() &&
                        y + player.getHeight(null) > block.getY() &&
                        y < block.getY() + block.getHeight()) {
                        canMove = false;
                        break;
                    }
                }

                if (canMove) {
                    x -= SPEED;
                    setPlayerLocation();
                }

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
                boolean canJump = true;
                // 플레이어가 점프할 때 위쪽 충돌 확인
                for (Block block : blocks) {
                    if (y > block.getY() &&
                        y < block.getY() + block.getHeight() &&
                        x + player.getWidth(null) > block.getX() &&
                        x < block.getX() + block.getWidth()) {
                        canJump = false;
                        break;
                    }
                }

                if (canJump) {
                    y -= JUMPSPEED;
                    setPlayerLocation();
                }

                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            // 점프 후에는 더 내려오도록 설정
            try {
                Thread.sleep(60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < 130 / JUMPSPEED; i++) {
                boolean canDescend = true;
                // 플레이어가 아래로 내려올 때 아래쪽 충돌 확인
                for (Block block : blocks) {
                    if (y + player.getHeight(null) > block.getY() &&
                        y + player.getHeight(null) < block.getY() + block.getHeight() &&
                        x + player.getWidth(null) > block.getX() &&
                        x < block.getX() + block.getWidth()) {
                        canDescend = false;
                        break;
                    }
                }

                if (canDescend) {
                    y += JUMPSPEED;
                    setPlayerLocation();
                }

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
    
 // 키 누름 이벤트 처리 메소드
    private void handleKeyPress(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_RIGHT) {
            isRight = true;
            moveRight();
        } else if (key == KeyEvent.VK_LEFT) {
            isLeft = true;
            moveLeft();
        } else if (key == KeyEvent.VK_UP) {
            if (!isJump) {
                isJump = true;
                jump();
            }
        }
    }

    // 키 뗌 이벤트 처리 메소드
    private void handleKeyRelease(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_RIGHT) {
            isRight = false;
        } else if (key == KeyEvent.VK_LEFT) {
            isLeft = false;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // 배경 이미지 그리기
        g.drawImage(backgroundImage, 0, 0, this);

        // 캐릭터 이미지 그리기
        g.drawImage(player, x, y, this);
        
        // 블록 그리기
        for (Block block : blocks) {
            g.setColor(Color.GRAY); // 원하는 색상으로 조정
            g.fillRect(block.getX(), block.getY(), block.getWidth(), block.getHeight());
        }
    }

}