package org.kgusarov.integration.spring.netty.etc;

import org.kgusarov.integration.spring.netty.events.TcpEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class TcpEventStack extends ArrayList<TcpEvent<?>> {
}
