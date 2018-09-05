package global.sesoc.www.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import global.sesoc.www.dao.T_GRequestRepository;
import global.sesoc.www.dao.T_GroupRepository;
import global.sesoc.www.dto.T_GRequest;
import global.sesoc.www.dto.T_Group;

@Controller
public class T_GrequestController {
	@Autowired
	T_GRequestRepository T_GRequestRepository;
	@Autowired
	T_GroupRepository T_GroupRepository;
	
	@ResponseBody
	@RequestMapping(value="/checkGrequest", method=RequestMethod.POST)
	public int checkGrequest(@RequestBody T_GRequest gRequest, HttpSession session) {
		String userId=(String)session.getAttribute("loginId"); //현재 유저 id
		
		T_Group g=new T_Group(); 	g.setGroNum(gRequest.getGroNum());
		T_Group group=T_GroupRepository.selectOneGroup(g);
		String groupUserId=group.getUserId();	//그룹 장의 id
		
		List<T_GRequest> gList=T_GRequestRepository.selectGrequest(gRequest);
		int result=1;
		
		if(userId.equals(groupUserId)) {//가입신청 유저가 모임장일 경우
			result=0;
			return result;
		}
		for (int i = 0; i < gList.size(); i++) {
			if(gList.get(i).getIsgreqAccepter()==0) {
				if(gList.get(i).getgRequester().equals(userId)) {
					result=2;	//가입 신청자가 다시 가입 신청을 누를 때
				}
			}else if (gList.get(i).getIsgreqAccepter()==1) {
				if(gList.get(i).getgRequester().equals(userId)) {
					result=3;	//해당 모임의 회원(이미 가입 된 유저)이 가입신청을 누를 때  
				}
			}
		}
		return result;
	}	
	@ResponseBody
	@RequestMapping(value="/insertGrequest",method=RequestMethod.POST)
	public int insertGrequest(@RequestBody T_GRequest gRequest,  HttpSession session) {
		
		T_Group g=new T_Group();  	g.setGroNum(gRequest.getGroNum());
		T_Group group=T_GroupRepository.selectOneGroup(g);
		gRequest.setGreqAccepter(group.getUserId());
		
		String gRequester=(String)session.getAttribute("loginId");
		gRequest.setgRequester(gRequester);
		
		int result=T_GRequestRepository.insertGrequest(gRequest);
		return result;
	}
	@ResponseBody
	@RequestMapping(value="/isAccepted",method=RequestMethod.POST)
	 public int isAccepted(HttpSession session) {
		int result=1;
		
		 String userId=(String)session.getAttribute("loginId");
		 List<T_GRequest> list=T_GRequestRepository.groupAccept(userId);
		 if(list.size()==0) {
			 result=0;
		 }
		 return result;
	 }
	@RequestMapping(value="/groupApply",method=RequestMethod.GET)
	public String groupApply(HttpSession session,Model model) {
		String userId=(String)session.getAttribute("loginId");
		List<T_GRequest> list=T_GRequestRepository.groupAccept(userId);
		model.addAttribute("gList",list);
		return "group/groupApply";
	}
}
