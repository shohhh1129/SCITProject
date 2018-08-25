package global.sesoc.www.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public class FileService {

	/**
	 * 업로드 된 파일을 지정된 경로에 저장하고, 저장된 파일명을 리턴
	 * @param mfile 업로드된 파일
	 * @param path 저장할 경로
	 * @return 저장된 파일명
	 */
	public static String saveFile(MultipartFile upload, String uploadPath) {
		//저장 폴더가 없으면 생성 - 원래는 controller writeBoard에 포함되었으나 따로 분리함
		File path = new File(uploadPath);
		if (!path.isDirectory()) {
			path.mkdirs();
		}

		//원본 파일명
		String originalFilename = upload.getOriginalFilename();
		
		//저장할 파일명 뒤에 을 오늘 날짜의 년월일로 생성
		//*SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		// 날짜나 랜덤값을 붙여 저장할 파일명을 작성한다. 랜덤 데이터 크기 36 - 더 자주 사용
		String sdf = UUID.randomUUID().toString();
		
		//원본 파일의 확장자와 파일명 분리
		String filename;		// 확장자를 뺀 파일명
		String ext;				// 확장명
		String savedFilename;	// 디스크에 저장할 이름
		
		//확장자 앞 '.'을 찾기 - 거기서부터 확장자를 제외한 부분을 분리하기
		int lastIndex = originalFilename.lastIndexOf('.');
		filename= originalFilename.substring(0, lastIndex);
		
		//확장자가 없는 경우
		if (lastIndex == -1) {
			ext = "";
		}
		
		//확장자가 있는 경우
		else {
			ext = "." + originalFilename.substring(lastIndex + 1);
		}
		
		// DB에 저장될 파일명 설정 - simpledataformat쓰는 경우
		//savedFilename = filename+"_"+sdf.format(new Date()) + ext;
		// DB에 저장될 파일명 설정 - UUID쓸 경우
		savedFilename = filename+"_"+sdf+ ext;
		
		//저장할 전체 경로를 포함한 File 객체 = fullPath
		File serverFile = null;
		
		//HDD에 저장할 파일명. 같은 이름의 파일이 있는 경우의 처리
		while (true) {
			serverFile = new File(uploadPath + "/" + savedFilename);
			//같은 이름의 파일이 없으면 나감.
			if ( !serverFile.isFile()) break;	
			
			int lastIndex1 = originalFilename.lastIndexOf('.');
			filename= originalFilename.substring(0, lastIndex1);
			
			//확장자가 없는 경우
			if (lastIndex1 == -1) {
				ext = "";
			}
			
			//확장자가 있는 경우
			else {
				ext = "." + originalFilename.substring(lastIndex1 + 1);
			}
			//같은 이름의 파일이 있으면 이름 뒤에 long 타입의 시간정보를 덧붙임.
			savedFilename = savedFilename + new Date().getTime()+ext;
		}		
		
		//파일 저장
		try {
			upload.transferTo(serverFile);  // 지정된 이름으로 지정된 위치에 파일 저장 
		} catch (Exception e) {
			savedFilename = null;
			e.printStackTrace();
		}
		
		return savedFilename;
	}
	
	/**
	 * 서버에 저장된 파일의 전체 경로를 전달받아, 해당 파일을 삭제
	 * @param fullpath 삭제할 파일의 경로
	 * @return 삭제 여부
	 */
	public static boolean deleteFile(String fullpath) {
		//파일 삭제 여부를 리턴할 변수
		boolean result = false;
		
		//전달된 전체 경로로 File객체 생성
		File delFile = new File(fullpath);
		
		//해당 파일이 존재하면 삭제
		if (delFile.isFile()) {
			delFile.delete();
			result = true;
		}
		
		return result;
	}
}
