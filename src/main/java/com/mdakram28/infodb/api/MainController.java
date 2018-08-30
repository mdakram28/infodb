package com.mdakram28.infodb.api;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.validation.ReportAsSingleViolation;

import org.apache.commons.collections4.ListUtils;
import org.mockito.internal.util.collections.ListUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.mdakram28.infodb.InfoList;
import com.mdakram28.infodb.datamanager.DataManager;

import edu.emory.mathcs.backport.java.util.LinkedList;
import edu.emory.mathcs.backport.java.util.Queue;

@Controller
public class MainController {

	@Autowired
	DataManager dataManagerService;

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${infofetch_batchsize:50}")
	int batchSize;

	@GetMapping("/details/person")
	@ResponseBody
	public InfoList getPersonDetails(@RequestParam(name = "name", required = true) String name,
			@RequestParam(name = "force", required = false, defaultValue = "false") boolean force)
			throws InterruptedException, ExecutionException {
		Future<InfoList> ret = dataManagerService.getPersonDetailsByName(name, force);
		return ret.get();
	}

	@PostMapping(value = "/details/multiple", consumes = "application/json", produces = "text/csv")
	@ResponseStatus(value = HttpStatus.OK)
	public void scrapeMultiplePeople(@RequestBody List<String> allNames, OutputStream out) throws IOException {
		int i = 0;
		for (List<String> names : ListUtils.partition(allNames, batchSize)) {
			List<Future<InfoList>> futureInfos = names.stream()
					.map(name -> dataManagerService.getPersonDetailsByName(name, false)).collect(Collectors.toList());

			logger.info("****************Submitted batch****************");
			out.write(("# Running batch...\n").getBytes());
			// PrintWriter p = new PrintWriter(out);
			for (Future<InfoList> futureInfo : futureInfos) {
				try {
					i++;
					InfoList infoList = futureInfo.get();
					out.write((i + "").getBytes());
					out.write(("\t").getBytes());
					out.write(infoList.getKey().getBytes());
					out.write(("\t").getBytes());
					out.write((infoList.getData().size() + "").getBytes());
					out.write(("\n").getBytes());
					out.flush();
					logger.info("******************************" + i);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			;
		}
		// p.close();
		out.close();
	}

}