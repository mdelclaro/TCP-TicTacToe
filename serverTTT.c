/*
 *
 *    TCPTacToe Server
 *
 *    Matheus Del Claro
 * 
 *    João Eduardo
 * 
 */

#include <pthread.h> 
#include <stdio.h>
#include <string.h> 
#include <sys/socket.h>
#include <arpa/inet.h> 
#include <unistd.h> 

int client_sock1, client_sock2;
char client_message[20] = "";
int connected = 0;

void receiveMessage(int *client);
 
void main() {	
    
	int socket_desc, c, write_size;	
	struct sockaddr_in server, client1, client2;
	pthread_t t1, t2;

	//Cria socket
	socket_desc = socket(AF_INET , SOCK_STREAM , 0);
	if (socket_desc == -1) {
		
		printf("Socket: Falha ao criar socket");
        return;
    }

	puts("Socket: Socket criado");
	 
	//Preenche a estrutura sockaddr_in
	server.sin_family = AF_INET;
	server.sin_addr.s_addr = INADDR_ANY;
	server.sin_port = htons(8888);
	 
	//Bind
	if (bind(socket_desc, (struct sockaddr *)&server, sizeof(server)) < 0) {

	    perror("Socket: bind falhou. Erro");
	    return;
	}

	puts("Socket: bind ok");
	
	//escuta
	listen(socket_desc, 3);

	while(1) {
		
        fflush(stdout);
        fflush(stdin);

        puts("Socket: Esperando por conexoes...");
        c = sizeof(struct sockaddr_in);
            
        //aceita conexão de cliente 1
        client_sock1 = accept(socket_desc, (struct sockaddr *)&client1, (socklen_t*)&c);

        if (client_sock1 < 0) {

            perror("Socket: accept falhou");
            return;        
        }
    
        puts("Socket: Conexao 1 aceita");

        //aceita conexão de cliente 2
        client_sock2 = accept(socket_desc, (struct sockaddr *)&client2, (socklen_t*)&c);

        if (client_sock2 < 0) {

            perror("Socket: accept falhou");
            return;
        }
    
        puts("Socket: Conexao 2 aceita");
    
        connected = 1;

        //cria 2 threads para manipular mensagens dos clientes
        pthread_create(&t1, NULL, (void *) receiveMessage, (int *) &client_sock1);
        pthread_create(&t2, NULL, (void *) receiveMessage, (int *) &client_sock2);
	
		while(connected) {
	
            //Verifica se mensagem é do tipo "slot"
			if (strlen(client_message) < 3 && strlen(client_message) > 0) {

				//Manda mensagem de volta para os dois clientes
				if ((write_size = send(client_sock1 , client_message, strlen(client_message), 0)) > 0)
					puts("INFO: Mensagem enviada cliente 1");
		
				if ((write_size = send(client_sock2 , client_message, strlen(client_message), 0)) > 0) 
                    puts("INFO: Mensagem enviada cliente 2");
		
			} 
		
			memset(client_message, 0, sizeof(client_message));	 
			fflush(stdin); 
	
		}
	
		close(client_sock1);
		close(client_sock2);
	
		pthread_join(t1, NULL);
		pthread_join(t2, NULL);
		
	}
}

void receiveMessage(int *client) {

	int read_size;

	//Recebe msg do cliente
	while(connected == 1) {
	
		if ((read_size = recv(*client , client_message , 20, 0)) > 0) {

			fflush(stdout);
			printf("INFO: Recebido -> %s", client_message);

		}
		
	}

	if (read_size == 0) {

		puts("Socket: Cliente disconectado");
		connected = 0;
		fflush(stdout);

	}

	else if (read_size == -1)
		perror("Socket: recv falhou");
		
}