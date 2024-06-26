package com.javalab.util;

/**
 * 페이징 네비게이터 함수
 * @author stoneis.pe.kr
 * @since 2013.07.10
 */
public class PageNavigator {

	public String getPageNavigator(int totalCount, int listCount, int pagePerBlock,
			int pageNum) {
		
		StringBuffer sb = new StringBuffer();	
		
		if(totalCount > 0) {	//총 레코드 수가 하나라도 있어야 페이징

			/*
			 * [총 페이지수]
			 * - 총게시물이 101개이고 한페이지에 10개의 페이지번호를 노출시켜야 된다고 가정.
			 * - 101 % 10 = 1이 되므로 총 페이지는 11이 됨.
			 */
			int totalNumOfPage = (totalCount % listCount == 0) ? totalCount / listCount :	totalCount / listCount + 1;
			
			/*
			 * [총 페이지 블럭]
			 * - 전체페이지가 11개이고 한페이지에 10개의 페이지번호를 표시한다고 가정하면 총 페이지블럭 2개가 됨.
			 * - 11 % 10 != 0이면 페이지 블럭은 2가 된다.[1...10] [11...20] 
			 * 
			 */
			int totalNumOfBlock = (totalNumOfPage % pagePerBlock == 0) ? totalNumOfPage / pagePerBlock : totalNumOfPage / pagePerBlock + 1;
			
			/*
			 * [요청된 페이지가 몇 번째 페이지 블럭(그룹)에 속하는지]
			 *  - 요청된 페이지의 숫자에 따라서 페이지 블럭이 결정됨.
			 * 전체 게시물수가 101개이고
			 * 1. 요청된 페이지가 10으로 나눈 나머지가 0일 경우
			 *   10 % 10(한페이지 노출 페이지수) = 몫은 0 나머지는 0
			 *   10 / 10 = 0 페이지 블럭 즉, [1,2,3,...10]
			 * 2. 요청된 페이지가 10으로 나눈 나머지가 0이 아닌 경우
			 *   11 % 10 = 몫은 1 나머지는 1
			 */
			int currentBlock = (pageNum % pagePerBlock == 0) ? 
					pageNum / pagePerBlock :	// 1페이지블럭([1...10])
					pageNum / pagePerBlock + 1;	// 2페이지블럭([11...20])
			
			System.out.println("currentBlock : " + currentBlock);
			
			/*
			 * 시작페이지 번호: (현재페이지블럭-1) * 10 + 1 = 1
			 */
			int startPage = (currentBlock - 1) * pagePerBlock + 1;

			/*
			 * 끝페이지 번호: 시작페이지 + 10 - 1 = 10
			 */
			int endPage = startPage + pagePerBlock - 1;

			/*
			 * 실제 끝페이지 구하기 : 
			 * - endPage : 계산 공식에 의해서 구해진 끝페이지 번호(20)
			 * - totalNumOfPage : 전체 페이지 갯수(11)
			 * - 실제 페이지 갯수가 11인데 계산 공식에 의해서 계산된 끝페이지는 20이다 
			 *   이럴 경우에는 실제페이지 개수인 11을 끝페이지로 한다.
			 */
			if(endPage > totalNumOfPage)
				endPage = totalNumOfPage;
			
			boolean isNext = false;
			boolean isPrev = false;
			
			/*
			 * 다음이 페이지가 있는지 표식 > >>
			 * - 현재 페이지블럭(1) < 전체페이지블럭(2) 표식을 노출시킨다.
			 */
			if(currentBlock < totalNumOfBlock)
				isNext = true;
			
			/*
			 * 이전 페이지가 있는지 표식 < <<
			 */
			if(currentBlock > 1)
				isPrev = true;
			
			/*
			 * 총 페이지 블럭이 단 한개인 경우 뒤로가기/앞으로가 표식 노출 안시킴.
			 */
			if(totalNumOfBlock == 1){
				isNext = false;
				isPrev = false;
			}
			
			if(pageNum > 1){	// 쿼리스트링에서 & = &amp; 
				sb.append("<a href=\"").append("boardList?pageNum=1");
				sb.append("\" title=\"<<\"><<</a>&nbsp;");
			}
			//
			if (isPrev) {	
				int goPrevPage = startPage - pagePerBlock;			
				sb.append("&nbsp;&nbsp;<a href=\"").append("boardList?pageNum="+goPrevPage+"");
				sb.append("\" title=\"<\"><</a>");
			} else {
				
			}
			for (int i = startPage; i <= endPage; i++) {
				if (i == pageNum) {
					sb.append("<a href=\"#\"><strong>").append(i).append("</strong></a>&nbsp;&nbsp;");
				} else {
					sb.append("<a href=\"").append("boardList?pageNum="+i);
					sb.append("\" title=\""+i+"\">").append(i).append("</a>&nbsp;&nbsp;");
				}
			}
			if (isNext) {
				int goNextPage = startPage + pagePerBlock;
	
				sb.append("<a href=\"").append("boardList?pageNum="+goNextPage);
				sb.append("\" title=\">\">></a>");
			} else {
				
			}
			if(totalNumOfPage > pageNum){
				sb.append("&nbsp;&nbsp;<a href=\"").append("boardList?pageNum="+totalNumOfPage);
				sb.append("\" title=\">>\">>></a>");
			}
		}
		
		return sb.toString();
	}
}
