import {
  CanActivate,
  ExecutionContext,
  Injectable,
  UnauthorizedException,
} from '@nestjs/common';
import { Reflector } from '@nestjs/core';
import { JwtService } from '@nestjs/jwt';

@Injectable()
export class RolesGuard implements CanActivate {
  constructor(
    private reflector: Reflector,
    private readonly jwtService: JwtService,
  ) {}

  canActivate(context: ExecutionContext): boolean {
    const roles = this.reflector.get<string[]>('roles', context.getHandler());
    if (!roles) {
      return true; // No roles specified, allow access
    }

    const request = context.switchToHttp().getRequest();
    const authHeader = request.headers.authorization;

    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      throw new UnauthorizedException(
        'Authorization token not provided or invalid',
      );
    }

    console.log(authHeader);

    const jwt = authHeader.replace('Bearer ', '');
    console.log('Decoded', jwt);
    const decoded = this.jwtService.decode(jwt);
    console.log('Decoded', decoded);

    if (!decoded || !decoded.role || !decoded.role.name) {
      throw new UnauthorizedException('Invalid or malformed token');
    }

    const userRole = decoded.role.name;
    console.log('Decoded user role:', userRole);
    console.log('Allowed roles:', roles);

    return roles.includes(userRole);
  }
}
