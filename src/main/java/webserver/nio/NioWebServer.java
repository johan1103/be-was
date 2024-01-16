package webserver.nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioWebServer {
  private static final String HOST = "localhost";
  private static final int PORT = 8080;
  private static final Logger logger = LoggerFactory.getLogger(NioWebServer.class);

  private Selector selector = null;
  private ServerSocketChannel serverSocketChannel = null;
  private ServerSocket serverSocket = null;

  public void init(){
    try {
      //셀렉터를 연다.
      selector = Selector.open();
      // 서버소켓채널을 생성한다.
      serverSocketChannel = ServerSocketChannel.open();
      serverSocketChannel.configureBlocking(false);
      // 서버 소켓 채널과 연결된 소켓을 가져온다.
      serverSocket = serverSocketChannel.socket();
      // 주어진 호스트와 포트로 소켓을 바인딩한다.
      InetSocketAddress isa = new InetSocketAddress(HOST,PORT);
      serverSocket.bind(isa);

      //서버소켓채널을 셀렉터에 등록한다.
      serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }catch (IOException e){
      logger.warn("Server.init()",e);
    }
  }
  public void start(){
    logger.info("Server is Started....");
    try {
      while (this.serverSocketChannel.isOpen()){
        logger.info("요청을 기다리는 중..");

        selector.select();
        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

        while(iterator.hasNext()){
          SelectionKey key = iterator.next();
          iterator.remove();

          if(key.isAcceptable()){
            accept(key);
          }if(key.isReadable()){
            read(key);
          }
        }
      }
    }catch (Exception e){
      logger.warn("Server start()",e);
    }
  }

  private void accept(SelectionKey key){
    ServerSocketChannel server = (ServerSocketChannel) key.channel();
    SocketChannel sc;
    try {
      // 서버소켓채널의 accept() 메서드로 서버소켓을 생성한다.
      sc = server.accept();

      registerChannel(selector, sc, SelectionKey.OP_READ);
      System.out.println(sc.toString() + " 클라이언트가 접속했습니다.");

    } catch (IOException e) {
      logger.warn("Server.accept()", e);
    }
  }

  private void registerChannel(Selector selector, SocketChannel sc, int opCode) throws IOException{
    if(sc==null){
      logger.info("Invalid Connection");
      return;
    }
    sc.configureBlocking(false);
    sc.register(selector, opCode);

  }
  private void read(SelectionKey key){
    //SelectionKey로부터 소켓 채널을 얻어온다.
    SocketChannel sc = (SocketChannel) key.channel();

    // ByteBuffer를 생성한다. (DMA, 초기 생성시간 느리지만 이후에
    // 커널이 JVM에 버퍼값을 복사할 필요가 없어져 read&write속도 빠름)
    ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

    try {
      int read = sc.read(buffer);
      if(read == -1) {
        sc.socket().close();
        sc.close();
        //removeUser(sc);
        System.out.println(sc.toString() + " 클라이언트가 접속을 해제하였습니다.");
      }
    } catch (IOException e) {
      try {
        sc.close();
      } catch (IOException ex) {
      }
      //removeUser(sc);
      System.out.println(sc.toString() + " 클라이언트가 접속을 해제하였습니다.");
    }

    try {
      /**
       * 읽은 ByteBuffer가 html 헤더값들로 파싱되는 로직 작성해야함.
       */
      sc.write(buffer);
    } catch (IOException e) {
      logger.warn("Server.broadcast()", e);
    }

  }

  public static void main(String[] args) {
    NioWebServer server = new NioWebServer();
    server.init();
    server.start();
  }
}
