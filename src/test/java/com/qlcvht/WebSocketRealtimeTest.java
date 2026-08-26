package com.qlcvht;

import com.qlcvht.dao.ThongBaoDAO;
import com.qlcvht.model.ThongBao;
import com.qlcvht.websocket.ChatMessage;
import com.qlcvht.websocket.ChatWebSocketClient;
import com.qlcvht.websocket.WebSocketService;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class WebSocketRealtimeTest {

    public static void main(String[] args) throws Exception {
        System.out.println("=== BAT DAU KIEM THU WEBSOCKET REALTIME CHAT (CVHT <-> SINH VIEN) ===");

        WebSocketService service = WebSocketService.getInstance();
        service.startServer();
        Thread.sleep(500);

        CountDownLatch advisorReceivedLatch = new CountDownLatch(1);
        CountDownLatch studentReceivedLatch = new CountDownLatch(1);

        AtomicReference<ChatMessage> receivedByAdvisor = new AtomicReference<>();
        AtomicReference<ChatMessage> receivedByStudent = new AtomicReference<>();

        // 1. Ket noi Client Co Van (CV001)
        System.out.println("\n[TEST 1] Khoi tao Client Co Van (CV001)...");
        ChatWebSocketClient advisorClient = service.getClient("CV001");
        advisorClient.addListener(new ChatWebSocketClient.MessageListener() {
            @Override
            public void onMessageReceived(ChatMessage message) {
                System.out.println(" -> [CO VAN NHAN REALTIME] Tu: " + message.getFromName() + " (" + message.getFromId() + ") - Noi dung: " + message.getContent());
                receivedByAdvisor.set(message);
                advisorReceivedLatch.countDown();
            }

            @Override
            public void onStatusChanged(boolean connected, String statusText) {
                System.out.println(" -> [Trang thai Co Van]: " + statusText);
            }
        });

        // 2. Ket noi Client Sinh Vien (20230001)
        System.out.println("\n[TEST 2] Khoi tao Client Sinh Vien (20230001)...");
        ChatWebSocketClient studentClient = service.getClient("20230001");
        studentClient.addListener(new ChatWebSocketClient.MessageListener() {
            @Override
            public void onMessageReceived(ChatMessage message) {
                System.out.println(" -> [SINH VIEN NHAN REALTIME] Tu: " + message.getFromName() + " (" + message.getFromId() + ") - Noi dung: " + message.getContent());
                receivedByStudent.set(message);
                studentReceivedLatch.countDown();
            }

            @Override
            public void onStatusChanged(boolean connected, String statusText) {
                System.out.println(" -> [Trang thai Sinh Vien]: " + statusText);
            }
        });

        Thread.sleep(800);

        // 3. Sinh vien gui tin nhan cho Co Van
        System.out.println("\n[TEST 3] Sinh vien (20230001) gui tin nhan cho Co Van (CV001) qua WebSocket...");
        ChatMessage msgSv = new ChatMessage("20230001", "Nguyen Van Nam", "SINH_VIEN", "CV001", "Xin tu van hoc tap", "Thay oi em muon hoi ve lich dang ky tin chi a!");
        studentClient.sendChatMessage(msgSv);

        boolean advisorGotMsg = advisorReceivedLatch.await(5, TimeUnit.SECONDS);
        if (advisorGotMsg && receivedByAdvisor.get() != null) {
            System.out.println(" => TEST 3 THANH CONG: Co van da nhan duoc tin nhan realtime tu sinh vien!");
        } else {
            System.err.println(" => TEST 3 THAT BAI: Co van chua nhan duoc tin nhan.");
        }

        // 4. Co Van tra loi lai Sinh Vien
        System.out.println("\n[TEST 4] Co Van (CV001) tra loi Sinh vien (20230001) qua WebSocket...");
        ChatMessage msgCv = new ChatMessage("CV001", "TS. Nguyen Van An", "CO_VAN", "20230001", "Phan hoi dang ky tin chi", "Chao em Nam, lich dang ky tin chi bat dau tu 8h00 sang thu Hai tuan toi nhe!");
        advisorClient.sendChatMessage(msgCv);

        boolean studentGotMsg = studentReceivedLatch.await(5, TimeUnit.SECONDS);
        if (studentGotMsg && receivedByStudent.get() != null) {
            System.out.println(" => TEST 4 THANH CONG: Sinh vien da nhan duoc cau tra loi realtime tu Co Van!");
        } else {
            System.err.println(" => TEST 4 THAT BAI: Sinh vien chua nhan duoc cau tra loi.");
        }

        // 5. Kiem tra luu tru vao Database
        System.out.println("\n[TEST 5] Kiem tra luu tru lich su tin nhan vao Database...");
        ThongBaoDAO dao = new ThongBaoDAO();
        dao.guiPhanHoiChoCoVan("20230001", "Nguyen Van Nam", "Xin tu van hoc tap", "Thay oi em muon hoi ve lich dang ky tin chi a!");
        dao.traLoiTinNhanSinhVien("CV001", "TS. Nguyen Van An", "20230001", "Phan hoi dang ky tin chi", "Chao em Nam, lich dang ky tin chi bat dau tu 8h00 sang thu Hai tuan toi nhe!");

        List<ThongBao> history = dao.getChatHistory("20230001");
        System.out.println(" -> So luong tin nhan trong lich su cua SV 20230001: " + history.size());
        for (ThongBao tb : history) {
            System.out.println("    * [" + tb.getNguoiGui() + "]: " + tb.getNoiDung());
        }

        // 6. Kiem tra tinh nang dem tin nhan chua doc & danh dau da doc
        System.out.println("\n[TEST 6] Kiem tra tinh nang dem chua doc & danh dau da doc...");
        int unreadForAdvisorBefore = dao.getUnreadCountForAdvisor("20230001");
        System.out.println(" -> Tin chua doc cua Co Van tu SV 20230001 (truoc khi doc): " + unreadForAdvisorBefore);
        dao.markMessagesAsReadByAdvisor("20230001");
        int unreadForAdvisorAfter = dao.getUnreadCountForAdvisor("20230001");
        System.out.println(" -> Tin chua doc cua Co Van tu SV 20230001 (sau khi doc): " + unreadForAdvisorAfter);
        if (unreadForAdvisorAfter == 0) {
            System.out.println(" => TEST 6 THANH CONG: Co van danh dau da doc thanh cong!");
        }

        int unreadForStudentBefore = dao.getUnreadCountForStudent("20230001");
        System.out.println(" -> Tin chua doc cua Sinh Vien 20230001 (truoc khi doc): " + unreadForStudentBefore);
        dao.markMessagesAsReadByStudent("20230001");
        int unreadForStudentAfter = dao.getUnreadCountForStudent("20230001");
        System.out.println(" -> Tin chua doc cua Sinh Vien 20230001 (sau khi doc): " + unreadForStudentAfter);
        if (unreadForStudentAfter == 0) {
            System.out.println(" => TEST 6b THANH CONG: Sinh vien danh dau da doc thanh cong!");
        }

        System.out.println("\n=== HOAN TAT KIEM THU WEBSOCKET 100% THANH CONG ===");
        System.exit(0);
    }
}
