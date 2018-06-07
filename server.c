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
char client_message[20] = "", ip[20] = "";
int connected = 0;

void receiveMessage(int *client);
 
void main() {	
    
	int socket_desc, c, write_size;	
	struct sockaddr_in server, client1, client2;
	pthread_t t1, t2;
	char ip_aux[20] = "";

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
            
        //aceita conexão de cliente 1 e cria thread
        client_sock1 = accept(socket_desc, (struct sockaddr *)&client1, (socklen_t*)&c);

        if (client_sock1 < 0) {

            perror("Socket: accept falhou");
            return;        
        }
    
        puts("Socket: Conexao 1 aceita");
        pthread_create(&t1, NULL, (void *) receiveMessage, (int *) &client_sock1);

        //aceita conexão de cliente 2 e cria thread
        client_sock2 = accept(socket_desc, (struct sockaddr *)&client2, (socklen_t*)&c);

        if (client_sock2 < 0) {

            perror("Socket: accept falhou");
            return;
        }
    
        puts("Socket: Conexao 2 aceita");
        pthread_create(&t2, NULL, (void *) receiveMessage, (int *) &client_sock2);
        
        connected = 1;    
        
		while(connected) {

			if ((strcmp(ip_aux, ip) != 0)) {
				printf("\nIP Anterior: %s", ip_aux);
				printf("\nNovo IP: %s\n", ip);
				strcpy(ip_aux, ip);

				//Verifica se mensagem é do tipo "slot"
				if (strlen(client_message) < 3 && strlen(client_message) > 0) {

					//Manda mensagem de volta para os dois clientes
					if ((write_size = send(client_sock1, client_message, strlen(client_message), 0)) > 0) 
						puts("INFO: Mensagem enviada cliente 1");
			
					if ((write_size = send(client_sock2, client_message, strlen(client_message), 0)) > 0) 
						puts("INFO: Mensagem enviada cliente 2");		
				}
			} //else printf("O mesmo jogador jogou de novo..");
		
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

	int read_size, write_size;
	char aux_msg[20] = "";
	char* token;
		
	if ((read_size = recv(*client , client_message , 20, 0)) > 0)
		puts("INFO: ack");

	//Recebe msg do cliente
	while(connected == 1) {
	
		if ((read_size = recv(*client , client_message , 20, 0)) > 0) {

			fflush(stdout);
			strcpy(aux_msg, client_message);
			printf("\nINFO: Recebido -> %s", client_message);
			
			token = strtok(aux_msg, ";");
			strcpy(ip, token);
			printf("\nIP: %s", ip);
			
			token = strtok(NULL, ";");
			strcpy(client_message, token);
			printf("\nMsg: %s", client_message);
		}
		
	}

	if (read_size == 0) {

		puts("Socket: Cliente disconectado");
		connected = 0;
		fflush(stdout);
	}

	else if (read_size == -1) perror("Socket: recv falhou");
		
}
