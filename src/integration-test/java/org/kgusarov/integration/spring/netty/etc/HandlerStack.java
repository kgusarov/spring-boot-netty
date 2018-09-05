package org.kgusarov.integration.spring.netty.etc;

import org.kgusarov.integration.spring.netty.events.TcpEventHandler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class HandlerStack extends ArrayList<TcpEventHandler<?>> {
}
