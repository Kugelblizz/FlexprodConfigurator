
package at.jku.cis.trustifier.blockchain;

import java.net.URI;
import java.text.MessageFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BlockchainRestClient {

	@Value("${spring.data.blockchain.uri}")
	private URI uri;

	@Autowired
	private RestTemplate restTemplate;

	public boolean isSimpleHashInBc(String hash) {
//		String requestUrl = format("{0}/api/queries/findSimpleHash?hash={1}", uri, hash);
//		ResponseEntity<SimpleHash[]> responseEntity = restTemplate.getForEntity(requestUrl, SimpleHash[].class);
//
//		SimpleHash[] objects = responseEntity.getBody();
//		if (Arrays.asList(objects).isEmpty()) {
//			return false;
//		} else {
//			return true;
//		}
		return true;
	}

	public SimpleHash postSimpleHash(String hash) {
		//String requestUrl = MessageFormat.format("{0}/api/simpleHash", uri);
		//return restTemplate.postForObject(requestUrl, hash, SimpleHash.class);
		return new SimpleHash("ABC");
	}

	public void deleteSimpleHash(String hash) {
		restTemplate.delete(MessageFormat.format("{0}/api/simpleHash/{1}", uri, hash));
	}
}